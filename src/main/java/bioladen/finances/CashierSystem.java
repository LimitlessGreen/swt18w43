package bioladen.finances;

import bioladen.customer.Customer;
import bioladen.customer.CustomerRepository;
import bioladen.product.ProductCatalog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author Lukas Petzold
 */
@Controller
@SessionAttributes({"shoppingCart"})

public class CashierSystem {

	private final ProductCatalog productCatalog;
	private final CustomerRepository customerRepository;

	CashierSystem(ProductCatalog productCatalog, CustomerRepository customerRepository) {
		this.productCatalog = productCatalog;
		this.customerRepository = customerRepository;
	}


	@ModelAttribute("shoppingCart")
	ShoppingCart initializeShoppingCart() {

		return new ShoppingCart();
	}

    // TODO Bitte nicht vergessen, für neues frontend in cashiersystem_new ändern
	@RequestMapping("/cashiersystem")
	public String cashiersystem(@ModelAttribute ShoppingCart shoppingCart, Model model) {

		return "cashiersystem";
	}


	/**
	 * Adds Products to ShoppingCart.
	 *
	 * @param product      gets added
	 * @param amount       times into the
	 * @param shoppingCart
	 * If no product is found an error message is returned
	 */
	@PostMapping("/cashiersystem")
	String addProduct(@RequestParam("pid") String product, @RequestParam("amount") long amount, @ModelAttribute ShoppingCart shoppingCart, Model model) {
		try {
			shoppingCart.addOrUpdateItem(productCatalog.findById(product).get(), amount);
			model.addAttribute("shoppingCart", shoppingCart);
		} catch (Exception e) {
			model.addAttribute("errorPid", true);
			model.addAttribute("errorMsgPid", "Kein Produkt gefunden");
		}

		return "cashiersystem";
	}


	/**
	 * Deletes the Item with the
	 * @param pid from the
	 * @param shoppingCart
	 */
	@PostMapping("/deleteCartItem")
	String deleteProduct(@RequestParam("productId") String pid, @ModelAttribute ShoppingCart shoppingCart) {
		shoppingCart.removeItem(pid);
		return "cashiersystem";
	}

	/**
	 * Calculates the change with the given
	 * @param changeInput  and the sum of the
	 * @param shoppingCart
	 * returns an error message if no changeInput is entered or
	 * if it is lower than the sum of the shoppingCart
	 */
	@PostMapping("/cashiersystemCalcChange")
	String calcChange(@RequestParam("changeInput") Double changeInput, @ModelAttribute ShoppingCart shoppingCart, Model model) {
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
	 * Tries finding the user with the
	 * @param userId
	 * @param model
	 * returns an error message when no user is found.
	 * if 0 is entered the discount will be 0% for a NormalCustomer
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


	@PostMapping("/cashiersystemFinish")
	String finish(@ModelAttribute ShoppingCart shoppingCart, Model model){
		shoppingCart.clear();

		return "redirect:/cashiersystem";
	}

}
