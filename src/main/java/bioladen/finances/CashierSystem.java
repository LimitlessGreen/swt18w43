package bioladen.finances;

import bioladen.product.ProductCatalog;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

/**
 * @author Lukas Petzold
 */
@Controller
@SessionAttributes({"cart"})

public class CashierSystem extends ShoppingCart {

	private final OrderManager<Order> orderManager;
	private final ProductCatalog productCatalog;

	CashierSystem(OrderManager<Order> orderManager, ProductCatalog productCatalog) {
		Assert.notNull(orderManager, "OrderManager must not be null!");
		this.orderManager = orderManager;
		this.productCatalog = productCatalog;
	}


	@ModelAttribute("shoppingCart")
	ShoppingCart initializeCart() {

		return new ShoppingCart();
	}


	@RequestMapping("/cashiersystem") //für neuese Cashiersystem ändern nicht vergessen
	public String cashiersystem() {
		return "cashiersystem"; //für neuese Cashiersystem : return "redirect:/cashiersystem_new";
	}

	@RequestMapping("/cart")
	String basket(@ModelAttribute ShoppingCart shoppingCart, Model model) {
		model.addAttribute("shoppingCart", shoppingCart);
		return "cart";
	}

	/**
	 * Adds Products to ShoppingCart
	 *
	 * @param product gets added
	 * @param amount times into the
	 * @param shoppingCart
	 * @return
	 */
	@PostMapping("/cashiersystem") //für neuese Cashiersystem ändern nicht vergessen
	String addProduct(@RequestParam("pid") String product, @RequestParam("amount") int amount, @ModelAttribute ShoppingCart shoppingCart, Model model) {
		try {
			shoppingCart.addOrUpdateItem(productCatalog.findById(product).get(), Quantity.of((long) amount));
		}
		catch (Exception e) {
			model.addAttribute("errorPid", true);
			model.addAttribute("errorMsgPid", "Kein Produkt gefunden");
			return "cashiersystem"; //für neuese Cashiersystem : return "redirect:/cashiersystem_new";
		}

		return "redirect:/cashiersystem"; //für neuese Cashiersystem : return "redirect:/cashiersystem_new";
	}

	/**
	 * Calculates the change with the given
	 * @param changeInput and the Sum of the
	 * @param shoppingCart
	 * @return
	 */
	@PostMapping("/cashiersystemCalcChange")
	String calcChange(@RequestParam("changeInput") Double changeInput, @ModelAttribute ShoppingCart shoppingCart, Model model) {
		try {
			BigDecimal money = BigDecimal.valueOf(changeInput);
			money = money.subtract(shoppingCart.getPrice());

			return "redirect:/cashiersystem?money=" + money;
		}
		catch (Exception e) {
			model.addAttribute("errorChange", true);
			model.addAttribute("errorMsgChange", "Bitte Betrag eingeben");

			return "cashiersystem";
		}
	}

	/**
	 * Tries finding the user with the
	 * @param userId
	 * @param model
	 * @return
	 */
	@PostMapping("/cashiersystemUser")
	String userId(@RequestParam("userId") String userId, Model model) {
		try {
			// TODO: find user

			return "cashiersystem";
		}
		catch (Exception e) {
			model.addAttribute("errorUid", true);
			model.addAttribute("errorMsgUid", "Kein Kunde gefunden");

			return "cashiersystem";
		}

	}

}
