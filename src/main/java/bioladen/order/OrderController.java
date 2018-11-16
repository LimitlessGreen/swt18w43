package bioladen.order;


import bioladen.finances.ShoppingCart;
import bioladen.product.DistributorProduct;
import bioladen.product.DistributorProductCatalog;
import bioladen.product.distributor.Distributor;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.OrderManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class OrderController {

	private OrderManager<Order> orderOrderManager;
	private DistributorProductCatalog distributorProductCatalog;

	public OrderController(OrderManager<Order> orderOrderManager, DistributorProductCatalog distributorProductCatalog) {
		this.orderOrderManager = orderOrderManager;
		this.distributorProductCatalog = distributorProductCatalog;
	}

	@GetMapping("/orders")
	public String orders(Model model, Cart cart, @RequestParam(value = "name", defaultValue = "") String name, @RequestParam(value = "amount", defaultValue = "1") Integer amount) {

		Iterable<DistributorProduct> distributorProducts = new ArrayList<>();

		if (name.length() > 0) {
			System.out.println("Filtering for " + name + " with amount " + amount);
			// TODO get products from catalog
			((ArrayList<DistributorProduct>) distributorProducts).removeIf(distributorProduct -> false);
			// TODO check when DistributorProduct ist implemented

		}

		int articles = 0;
		for (CartItem cartItem : cart) {
			articles++;
		}

		model.addAttribute("amount", amount);
		model.addAttribute("articles", articles);
		model.addAttribute("totalprice", cart.getPrice().signum());
		model.addAttribute("products", distributorProducts);

		return "order";
	}


	@GetMapping("/orders/complete")
	public String completeOrder(Model model, Cart cart) {

		if (cart.isEmpty()) {
			return "redirect:/orders";
		}


		Map<Distributor, Cart> distributorSetMap = new HashMap<>();

		int items = 0;
		for (CartItem cartItem : cart) {
			items++;
			distributorSetMap.computeIfAbsent(null /* TODO replace when DistributorProduct is a Product */, distributor -> new OrderCart()).addOrUpdateItem(cartItem.getProduct(), cartItem.getQuantity());
		}

		for ( Distributor distributor : distributorSetMap.keySet() ) {
			Order order = new Order( null ,distributor ); // TODO replace with actual user
			distributorSetMap.get(distributor).addItemsTo(order);
			orderOrderManager.save(order);
		}

		model.addAttribute("price", cart.getPrice().signum());
		model.addAttribute("productcount", items );
		model.addAttribute("distributorcount", distributorSetMap.size());

		return "orderfinished";
	}

	@GetMapping("/orders/remove")
	public String removeOrder(Model model, @RequestParam("id") String id, Cart cart) {

		cart.removeItem(id);

		return "redirect:/orders";
	}


	@GetMapping("/orders/add")
	public String addItem(Model model, Cart cart, @RequestParam("id") String id, @RequestParam("amount") Integer integer) {

		DistributorProduct product = null; // TODO get when catalog is implemented

		cart.addOrUpdateItem(null, integer); // TODO replace with product

		return "redirect:/orders";
	}


	@ModelAttribute("cart")
	OrderCart initializeCart() {

		return new OrderCart();
	}

}
