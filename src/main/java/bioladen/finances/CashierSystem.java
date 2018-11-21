package bioladen.finances;

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

	CashierSystem(ProductCatalog productCatalog) {
		this.productCatalog = productCatalog;
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
	 * Adds Products to ShoppingCart
	 *
	 * @param product      gets added
	 * @param amount       times into the
	 * @param shoppingCart
	 * @return
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

	@PostMapping("/deleteCartItem")
	String deleteProduct(@RequestParam("productId") String pid, @ModelAttribute ShoppingCart shoppingCart) {
		shoppingCart.removeItem(pid);
		return "cashiersystem";
	}

	/**
	 * Calculates the change with the given
	 *
	 * @param changeInput  and the Sum of the
	 * @param shoppingCart
	 * @return
	 */
	@PostMapping("/cashiersystemCalcChange")
	String calcChange(@RequestParam("changeInput") Double changeInput, @ModelAttribute ShoppingCart shoppingCart, Model model) {
		try {
			BigDecimal money = BigDecimal.valueOf(changeInput);
			money = money.subtract(shoppingCart.getPrice());
			model.addAttribute("change", money);

			return "cashiersystem";
		} catch (Exception e) {
			model.addAttribute("errorChange", true);
			model.addAttribute("errorMsgChange", "Bitte Betrag eingeben");

			return "cashiersystem";
		}
	}

	/**
	 * Tries finding the user with the
	 *
	 * @param userId
	 * @param model
	 * @return
	 */
	@PostMapping("/cashiersystemUser")
	String userId(@RequestParam("userId") String userId, Model model) {
		try {
			// TODO: find user

			return "cashiersystem";
		} catch (Exception e) {
			model.addAttribute("errorUid", true);
			model.addAttribute("errorMsgUid", "Kein Kunde gefunden");

			return "cashiersystem";
		}

	}

}
