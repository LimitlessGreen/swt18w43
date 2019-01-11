package bioladen.finances;

import bioladen.customer.Customer;
import bioladen.customer.CustomerRepository;
import bioladen.datahistory.DataHistoryManager;
import bioladen.datahistory.EntityLevel;
import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import lombok.RequiredArgsConstructor;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

public class CashierSystem {

	private final InventoryProductCatalog inventoryProductCatalog;
	private final CustomerRepository customerRepository;
	private final AuthenticationManager authenticationManager;

	@ModelAttribute("shoppingCart")
	ShoppingCart initializeShoppingCart() {

		return new ShoppingCart();
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@RequestMapping("/cashiersystem")
	public String cashiersystem(@ModelAttribute ShoppingCart shoppingCart, Model model) throws UnknownHostException {
		model.addAttribute("sc_id", shoppingCart.toString().substring(shoppingCart.toString().lastIndexOf('@') + 1));
		model.addAttribute("hostname", InetAddress.getLocalHost().getHostName());

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
			Model model) throws UnknownHostException {

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
				if (item.getQuantity() - 1 <= 0 && amount < 0) {
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

		model.addAttribute("sc_id", shoppingCart.toString().substring(shoppingCart.toString().lastIndexOf('@') + 1));
		model.addAttribute("hostname", InetAddress.getLocalHost().getHostName());

		return "cashiersystem";
	}

	/**
	 * Deletes an item from the shoppingCart.
	 *
	 * @param pid  The product with the String pid gets deleted.
	 */
	@PostMapping("/deleteCartItem")
	String deleteProduct(@RequestParam("cartItemId") String pid,
						 @ModelAttribute ShoppingCart shoppingCart,
						 Model model) throws UnknownHostException {
		shoppingCart.getItem(pid).get().getInventoryProduct()
				.removeDisplayedAmount(-(shoppingCart.getItem(pid).get().getQuantity()));
		inventoryProductCatalog.save(shoppingCart.getItem(pid).get().getInventoryProduct());
		shoppingCart.removeItem(pid);

		model.addAttribute("sc_id", shoppingCart.toString().substring(shoppingCart.toString().lastIndexOf('@') + 1));
		model.addAttribute("hostname", InetAddress.getLocalHost().getHostName());

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
			Model model) throws UnknownHostException {

		model.addAttribute("sc_id", shoppingCart.toString().substring(shoppingCart.toString().lastIndexOf('@') + 1));
		model.addAttribute("hostname", InetAddress.getLocalHost().getHostName());

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
	String userId(@RequestParam("userId") long userId,
				  @ModelAttribute ShoppingCart shoppingCart,
				  Model model) throws UnknownHostException {
		model.addAttribute("sc_id", shoppingCart.toString().substring(shoppingCart.toString().lastIndexOf('@') + 1));
		model.addAttribute("hostname", InetAddress.getLocalHost().getHostName());

		try {
			Customer customer = customerRepository.findById(userId).get();
			shoppingCart.setCustomer(customer);
			String maxPurchasedProduct = customer.getMaxPurchasedProduct();
			model.addAttribute("recommendedProduct",
					(maxPurchasedProduct == null)
							? "Zur Zeit gibt es hier keine Empfehlungen"
							: maxPurchasedProduct);

			return "cashiersystem";
		} catch (Exception e) {
			model.addAttribute("errorUid", true);
			model.addAttribute("errorMsgUid", "Kein Kunde gefunden");

			return "cashiersystem";
		}
	}

	/**
	 * Adds the pfandValue of a product to the pfand map of the ShoppingCart.
	 *
	 * @param product The product with the pid.
	 * @param amount The amount of the product to be added to the pfand map.
	 * @param shoppingCart the pfandValues get added the the pfand map in the ShoppingCart.
	 */
	@PostMapping("/cashiersystemPfand")
	String addPfand(@RequestParam("pid") Long product,
					@RequestParam("amount") Long amount,
					@ModelAttribute ShoppingCart shoppingCart,
					Model model) throws UnknownHostException {
		try {
			shoppingCart.addOrUpdatePfand(inventoryProductCatalog.findById(product).get().getPfandPrice(), amount);
		} catch (Exception e) {
			model.addAttribute("errorPfand", true);
			model.addAttribute("errorPfandMsg", "Ungültiges Produkt eingegeben");
		}

		model.addAttribute("sc_id", shoppingCart.toString().substring(shoppingCart.toString().lastIndexOf('@') + 1));
		model.addAttribute("hostname", InetAddress.getLocalHost().getHostName());

		return "cashiersystem";
	}

	/**
	 * Finish the sale and clears the shoppingCart.
	 * Pushes a Created Event for the sale.
	 *
	 * @param shoppingCart gets cleared and user set to null.
	 */
	@PostMapping("/cashiersystemFinish")
	String finish(@ModelAttribute ShoppingCart shoppingCart, Model model) throws UnknownHostException {

		ShoppingCartSale shoppingCartSale = new ShoppingCartSale(shoppingCart.getCustomer(),
																 shoppingCart.getPfandMoney(),
																 shoppingCart.getMwstMoney(),
																 shoppingCart.getSaleMoney());

		Customer customer = shoppingCart.getCustomer();
		if (customer != null) {
			for (Map.Entry<InventoryProduct, CartCartItem> item : shoppingCart.getItems().entrySet()) {
				customer.addPurchase(item.getKey());
			}
			customerRepository.save(customer);
		}

		// (👁 ᴥ 👁) Event
		pushShoppingCart(shoppingCartSale, EntityLevel.CREATED, "Verkauf");

		shoppingCart.clear();

		model.addAttribute("sc_id", shoppingCart.toString().substring(shoppingCart.toString().lastIndexOf('@') + 1));
		model.addAttribute("hostname", InetAddress.getLocalHost().getHostName());

		return "redirect:/cashiersystem";
	}

	/**
	 * Aborts the sale and clears the shoppingCart.
	 * Pushes a Deleted Event for the cancellation.
	 *
	 * @param shoppingCart gets cleared and user set to null.
	 */
	@PostMapping("/cashiersystemAbort")
	String abort(@ModelAttribute ShoppingCart shoppingCart, Model model) throws UnknownHostException {

		ShoppingCartCancel shoppingCartCancel = new ShoppingCartCancel(shoppingCart.getCustomer(),
																	   shoppingCart.getAmountOfItems());

		// (👁 ᴥ 👁) Event
		pushShoppingCart(shoppingCartCancel, EntityLevel.DELETED, "Stornierung");

		for (Map.Entry<InventoryProduct, CartCartItem> e : shoppingCart.getItems().entrySet()) {
			e.getKey().removeDisplayedAmount(-(e.getValue().getQuantity()));
			inventoryProductCatalog.save(e.getKey());
		}

		shoppingCart.clear();

		model.addAttribute("sc_id", shoppingCart.toString().substring(shoppingCart.toString().lastIndexOf('@') + 1));
		model.addAttribute("hostname", InetAddress.getLocalHost().getHostName());

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
	private final DataHistoryManager<ShoppingCart> dataHistoryManager;

	private void pushShoppingCart(ShoppingCart shoppingCart, EntityLevel entityLevel, String name) {
		Optional<UserAccount> currentUser = this.authenticationManager.getCurrentUser();
		dataHistoryManager.push(
				name,
				shoppingCart,
				entityLevel,
				currentUser.orElse(null));
	}
}
