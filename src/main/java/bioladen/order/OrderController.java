package bioladen.order;


import bioladen.product.distributor.Distributor;
import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import org.salespointframework.order.OrderManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@SessionAttributes("cart")
public class OrderController {

	private OrderManager<Order> orderOrderManager;
	private DistributorProductCatalog distributorProductCatalog;

	public OrderController(OrderManager<Order> orderOrderManager, DistributorProductCatalog distributorProductCatalog) {
		this.orderOrderManager = orderOrderManager;
		this.distributorProductCatalog = distributorProductCatalog;
	}

	@GetMapping("/orders")
	public String orders(Model model, @ModelAttribute("cart") OrderCart cart, @RequestParam(value = "name", defaultValue = "") String name, @RequestParam(value = "amount", defaultValue = "1") Integer amount) {


		Iterable<DistributorProduct> distributorProducts = new ArrayList<>();

		if (name.length() > 0) {
			System.out.println("Filtering for " + name + " with amount " + amount);
			((ArrayList<DistributorProduct>) distributorProducts).addAll(distributorProductCatalog.findAll());

			((ArrayList<DistributorProduct>) distributorProducts).removeIf(distributorProduct -> {
				System.out.println(distributorProduct.getDistributorProductIdentifier());
				if (!distributorProduct.getName().contains(name)) {
					return true;
				}
				if (distributorProduct.getMinimumOrderAmount() * distributorProduct.getUnit().doubleValue() > amount) {
					return true;
				}
				return false;
			});
		}

		double total = 0;
		for (OrderCartItem orderCartItem : cart) {
				total += orderCartItem.getPrice().getNumber().doubleValue() * orderCartItem.getQuantity().getAmount().doubleValue();
		}

		model.addAttribute("amount", amount);
		model.addAttribute("articles", cart.get().count());
		model.addAttribute("totalprice", String.format("%.2f", total));
		model.addAttribute("products", distributorProducts);

		return "order";
	}


	@GetMapping("/orders/orderfinished")
	public String completeOrder(Model model, @ModelAttribute("cart") OrderCart cart) {

		if (cart.isEmpty()) {
			return "redirect:/orders";
		}


		Map<Distributor, OrderCart> distributorSetMap = new HashMap<>();

		int items = 0;
		for (OrderCartItem cartItem : cart) {
			items++;
			distributorSetMap.computeIfAbsent(cartItem.getProduct().getDistributor(), distributor -> new OrderCart()).addOrUpdateItem(cartItem.getProduct(), cartItem.getQuantity());
		}

		for (Distributor distributor : distributorSetMap.keySet()) {
			Order order = new Order(null, distributor); // TODO replace with actual user
			//distributorSetMap.get(distributor).addItemsTo(order);
			orderOrderManager.save(order);
		}

		model.addAttribute("price", cart.getPrice().signum());
		model.addAttribute("productcount", items);
		model.addAttribute("distributorcount", distributorSetMap.size());

		return "orderfinished";
	}

	@GetMapping("/orders/remove")
	public String removeOrder(Model model, @RequestParam("id") String id, @ModelAttribute("cart") OrderCart cart) {

		cart.removeItem(id);

		return "redirect:/orders";
	}


	@GetMapping("/orders/add")
	public String addItem(Model model, @ModelAttribute("cart") OrderCart cart, @RequestParam("id") String id, @RequestParam("amount") Integer integer) {

		Optional<DistributorProduct> product = distributorProductCatalog.findById(id);

		if (product.isPresent()) {
			cart.addOrUpdateItem(product.get(), integer);
		}

		return "redirect:/orders";
	}


	@ModelAttribute("cart")
	OrderCart initializeCart() {
		return new OrderCart();
	}

}
