package bioladen.finances;

import bioladen.customer.CustomerRepository;
import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import lombok.RequiredArgsConstructor;
import org.salespointframework.catalog.Product;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * A cashiersystem for the users to sell wares to the customers.
 *
 * @author Lukas Petzold
 */
@Controller
@RequiredArgsConstructor
@SessionAttributes({"shoppingCart"})

public class CashierSystem implements ApplicationEventPublisherAware {

	private final InventoryProductCatalog inventoryProductCatalog;
	private final CustomerRepository customerRepository;
	private final AuthenticationManager authenticationManager;

	@ModelAttribute("shoppingCart")
	ShoppingCart initializeShoppingCart() {

		return new ShoppingCart();
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@RequestMapping("/cashiersystem")
	public String cashiersystem(@ModelAttribute ShoppingCart shoppingCart, Model model) {

		return "cashiersystem";
	}

	/**
	 * Adds Products to ShoppingCart.
	 *
	 * @param product      An entity of a product gets added to the shoppingCart.
	 * @param amount       The number of times the product gets added.
	 * If no product is found an error message is returned.
	 */
	@PostMapping("/cashiersystemAdd")
	String addProduct(
			@RequestParam("pid") Long product,
			@RequestParam("amount") Long amount,
			@ModelAttribute ShoppingCart shoppingCart,
			Model model) {

		final Long EAN13 = 2000000000000L;
		final int EAN13_LENGTH = 13;

		try {
			if (product >= EAN13 && product.toString().length() == EAN13_LENGTH && product.toString().startsWith("2")) {
				product = InventoryProduct.fromEan13(product);
			}
			if (inventoryProductCatalog.findById(product).get().getDisplayedAmount() >= amount) {
				inventoryProductCatalog.findById(product).get().removeDisplayedAmount(amount);
				CartCartItem item = shoppingCart.addOrUpdateItem(inventoryProductCatalog.findById(product).get(), amount);
				inventoryProductCatalog.save(inventoryProductCatalog.findById(product).get());
				if (item.getQuantity() - 1 == 0 && amount < 0) {
					shoppingCart.removeItem(item.getId());			// delete item when amount is negative and the remaining quantity is 1
				}
			} else {
				model.addAttribute("errorProductAmount", true);
				model.addAttribute("errorProductAmountMsg", "Vom angegebenen Produkt ist weniger vorhanden als eingegeben");
			}
			model.addAttribute("shoppingCart", shoppingCart);
		} catch (Exception e) {
			model.addAttribute("errorPid", true);
			model.addAttribute("errorMsgPid", "Kein Produkt gefunden");
		}

		return "cashiersystem";
	}

	/**
	 * Deletes an item from the shoppingCart.
	 *
	 * @param pid  The product with the String pid gets deleted.
	 */
	@PostMapping("/deleteCartItem")
	String deleteProduct(@RequestParam("cartItemId") String pid, @ModelAttribute ShoppingCart shoppingCart) {
		shoppingCart.getItem(pid).get().getInventoryProduct()
				.removeDisplayedAmount(-(shoppingCart.getItem(pid).get().getQuantity()));
		inventoryProductCatalog.save(shoppingCart.getItem(pid).get().getInventoryProduct());
		shoppingCart.removeItem(pid);
		return "cashiersystem";
	}

	/**
	 * Calculates the change with the money from the customer and the sum of the shoppingCart.
	 *
	 * @param changeInput  The amount of money given by the customer.
	 * @param shoppingCart To calculate the change, the sum of the shoppingCart ist needed.
	 * returns an error message if no changeInput is entered or
	 *                     if it is lower than the sum of the shoppingCart.
	 */
	@PostMapping("/cashiersystemCalcChange")
	String calcChange(
			@RequestParam("changeInput") Double changeInput,
			@ModelAttribute ShoppingCart shoppingCart,
			Model model) {
		try {
			BigDecimal money = BigDecimal.valueOf(changeInput);
			money = money.subtract(shoppingCart.getPrice());
			if (money.compareTo(BigDecimal.ZERO) < 0) {
				model.addAttribute("errorChangeLow", true);
				model.addAttribute("errorChangeLowMsg", "Eingegebener Betrag zu niedrig");
			} else {
				model.addAttribute("change", money);
			}

			return "cashiersystem";
		} catch (Exception e) {
			model.addAttribute("errorChange", true);
			model.addAttribute("errorMsgChange", "Bitte Betrag eingeben");

			return "cashiersystem";
		}
	}

	/**
	 * Adding a user to the shoppingCart so the Discount can be calculated into the sum.
	 *
	 * @param userId  The user with the userId gets added.
	 * returns an error message when no user is found.
	 * if 0 is entered the discount will be 0% for a NormalCustomer.
	 */
	@PostMapping("/cashiersystemUser")
	String userId(@RequestParam("userId") long userId, @ModelAttribute ShoppingCart shoppingCart, Model model) {
		try {
			shoppingCart.setCustomer(customerRepository.findById(userId).get());

			return "cashiersystem";
		} catch (Exception e) {
			model.addAttribute("errorUid", true);
			model.addAttribute("errorMsgUid", "Kein Kunde gefunden");

			return "cashiersystem";
		}
	}

	@PostMapping("/cashiersystemPfand")
	String addPfand(@RequestParam("pid") Long product,
					@RequestParam("amount") Long amount,
					@ModelAttribute ShoppingCart shoppingCart,
					Model model){
		try {
			shoppingCart.addOrUpdatePfand(inventoryProductCatalog.findById(product).get().getPfandPrice(), amount);
		} catch (Exception e) {
			model.addAttribute("errorPfand", true);
			model.addAttribute("errorPfandMsg", "Ungültiges Produkt eingegeben");
		}

		return "cashiersystem";
	}

	/**
	 * Finish the sale and clears the shoppingCart.
	 *
	 * @param shoppingCart gets cleared and user set to null.
	 */
	@PostMapping("/cashiersystemFinish")
	String finish(@ModelAttribute ShoppingCart shoppingCart, Model model) {
		// (👁 ᴥ 👁) Event
		publishEvent(shoppingCart, EntityLevel.CREATED, "Verkauf");

		shoppingCart.clear();

		return "redirect:/cashiersystem";
	}

	/**
	 * Aborts the sale and clears the shoppingCart.
	 *
	 * @param shoppingCart gets cleared and user set to null.
	 */
	@PostMapping("/cashiersystemAbort")
	String abort(@ModelAttribute ShoppingCart shoppingCart, Model model) {

		// (👁 ᴥ 👁) Event
		publishEvent(shoppingCart, EntityLevel.DELETED, "Stornierung");

		for (Map.Entry<InventoryProduct, CartCartItem> e : shoppingCart.getItems().entrySet()) {
			e.getKey().removeDisplayedAmount(-(e.getValue().getQuantity()));
			inventoryProductCatalog.save(e.getKey());
		}

		shoppingCart.clear();

		return "redirect:/cashiersystem";
	}

	/*
         _________________
        < Event publisher >
         -----------------
            \   ^__^
             \  (@@)\_______
                (__)\       )\/\
                    ||----w |
                    ||     ||

    */
	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	private void publishEvent(ShoppingCart shoppingCart, EntityLevel entityLevel, String name) {
		Optional<UserAccount> currentUser = this.authenticationManager.getCurrentUser();
		publisher.publishEvent(new EntityEvent<>(
				name,
				shoppingCart,
				entityLevel,
				currentUser.orElse(null)));
	}
}
