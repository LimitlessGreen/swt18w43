package bioladen.finances;

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

/**
 * @author Lukas Petzold
 */
@Controller
@SessionAttributes({"cart"})

public class CashierSystem extends ShoppingCart {

	private final OrderManager<Order> orderManager;
	private final Inventory<InventoryItem> inventory;

	CashierSystem(OrderManager<Order> orderManager, Inventory<InventoryItem> inventory) {
		Assert.notNull(orderManager, "OrderManager must not be null!");
		Assert.notNull(inventory, "Inventory must not be null");
		this.orderManager = orderManager;
		this.inventory = inventory;
	}


	@ModelAttribute("cart")
	ShoppingCart initializeCart() {

		return new ShoppingCart();
	}


	@RequestMapping("/cashiersystem")
	public String cashiersystem() {
		return "cashiersystem";
	}

	@RequestMapping("/cart")
	String basket() {
		return "cart";
	}

	/**
	 * Adds Products to ShoppingCart
	 *
	 * @param product gets added
	 * @param amount times into the
	 * @param cart
	 * @return
	 */
	@PostMapping("/cashiersystem")
	String addProduct(@RequestParam("pid") ProductIdentifier product, @RequestParam("amount") int amount, @ModelAttribute Cart cart, Model model) {
		try {
			cart.addOrUpdateItem(inventory.findByProductIdentifier(product).get().getProduct(), Quantity.of((long) amount));
		}
		catch (Exception e) {
			model.addAttribute("errorPid", true);
			model.addAttribute("errorMsgPid", "Kein Produkt gefunden");
			return "cashiersystem";
		}

		return "redirect:/cashiersystem";
	}

	/**
	 * Calculates the change with the given
	 * @param changeInput and the Sum of the
	 * @param cart
	 * @return
	 */
	@PostMapping("/cashiersystemCalcChange")
	String calcChange(@RequestParam("changeInput") Double changeInput, @ModelAttribute Cart cart, Model model) {
		try {
			MonetaryAmount money = Money.of(changeInput, Monetary.getCurrency("EUR"));
			money = money.subtract(cart.getPrice());

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
