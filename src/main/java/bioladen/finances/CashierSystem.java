package bioladen.finances;

import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


@Controller
@SessionAttributes({"cart"})


public class CashierSystem extends Cart {

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
	Cart initializeCart() {

		return new Cart();
	}


	@RequestMapping("/cashiersystem")
	public String cashiersystem() {
		return "cashiersystem";
	}

	@RequestMapping("/cart")
	String basket() {
		return "cart";
	}


	@PostMapping("/cart")
	String addProduct(@RequestParam("pid") Product product, @RequestParam("number") int number, @ModelAttribute Cart cart) {
		int amount = number;
		cart.addOrUpdateItem(product, Quantity.of((long)amount));

		return "redirect:cart";
	}

}
