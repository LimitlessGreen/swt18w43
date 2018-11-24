package bioladen.order;


import bioladen.customer.CustomerRepository;
import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import bioladen.product.distributor.Distributor;
import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.math.BigDecimal;
import java.util.*;

@Controller
@SessionAttributes("cart")
public class OrderController {

	private final DistributorProductCatalog distributorProductCatalog;

	public OrderController(DistributorProductCatalog distributorProductCatalog) {
		this.distributorProductCatalog = distributorProductCatalog;


	}

	@GetMapping("/orders")
	public String orders(Model model, @ModelAttribute("cart") OrderCart cart, @RequestParam(value = "name", defaultValue = "") String name, @RequestParam(value = "amount", defaultValue = "1") Integer amount) {
		Iterable<DistributorProduct> distributorProducts = new ArrayList<>();
		List<DistributorProduct> filtered = new ArrayList<>();


		if (name.length() > 0) {
			System.out.println("Filtering for " + name + " with amount " + amount);
			((ArrayList<DistributorProduct>) distributorProducts).addAll(distributorProductCatalog.findAll());

			((ArrayList<DistributorProduct>) distributorProducts).removeIf(distributorProduct -> {
				System.out.println(distributorProduct.getDistributorProductIdentifier());
				if (!distributorProduct.getName().contains(name)) {
					return true;
				}
				if (distributorProduct.getMinimumOrderAmount() * distributorProduct.getUnit().doubleValue() > amount) {
					filtered.add(distributorProduct);
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
		model.addAttribute("productsfiltered", filtered);
		return "order";
	}


	@GetMapping("/orders/orderfinished")
	public String completeOrder(Model model, @ModelAttribute("cart") OrderCart cart, @LoggedIn Optional<UserAccount> userAccount) {

		if (cart.isEmpty()) {
			return "redirect:/orders";
		}


		return userAccount.map(account -> {
			Map<Distributor, OrderCart> distributorSetMap = new HashMap<>();
			double total = 0;
			int items = 0;
			for (OrderCartItem cartItem : cart) {
				items++;
				distributorSetMap.computeIfAbsent(cartItem.getProduct().getDistributor(), distributor -> new OrderCart()).addOrUpdateItem(cartItem.getProduct(), cartItem.getQuantity());
				total += cartItem.getPrice().getNumber().doubleValue() * cartItem.getQuantity().getAmount().doubleValue();
			}

			for (Distributor distributor : distributorSetMap.keySet()) {
				/*Order order = new Order(account, distributor);
				order.addItems(cart);
				orderRepository.save(order);*/
			}

			model.addAttribute("totalprice", String.format("%.2f", total));
			model.addAttribute("productcount", items);
			model.addAttribute("distributorcount", distributorSetMap.size());

			return "orderfinished";
		}).orElse("redirect:/login");
	}

	@GetMapping("/orders/remove")
	public String removeOrder(Model model, @RequestParam("id") String id, @ModelAttribute("cart") OrderCart cart) {

		cart.removeItem(id);

		return "redirect:/orders";
	}


	@GetMapping("/orders/add")
	public String addItem(Model model, @ModelAttribute("cart") OrderCart cart, @RequestParam("id") Long id, @RequestParam("amount") Integer integer) {

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
