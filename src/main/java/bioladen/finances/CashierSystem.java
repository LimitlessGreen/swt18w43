package bioladen.finances;

import bioladen.product.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lukas Petzold
 */
@Controller
@SessionAttributes({"cart"})

public class CashierSystem extends ShoppingCart {

	private OrderManager<Order> orderManager;
	private Inventory<InventoryItem> inventory;

	CashierSystem() {

	}

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


	@PostMapping("/cashiersystem")
	String addProduct(@RequestParam("pid") ProductIdentifier product, @RequestParam("amount") int amount, @ModelAttribute Cart cart) {
		cart.addOrUpdateItem(inventory.findByProductIdentifier(product).get().getProduct(), Quantity.of((long) amount));

		return "redirect:/cart";
	}

}
