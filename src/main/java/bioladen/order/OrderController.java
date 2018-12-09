package bioladen.order;


import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import bioladen.product.distributor.Distributor;
import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import lombok.RequiredArgsConstructor;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@SessionAttributes("cart")
@RequiredArgsConstructor
public class OrderController {

	private final DistributorOrderRepository orderRepository;
	private final DistributorProductCatalog distributorProductCatalog;
	private final InventoryProductCatalog inventoryProductCatalog;
	private final OrderItemRepository itemRepository;


	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/orders")
	public String orders(Model model) {

		model.addAttribute("orders", orderRepository.findAll());

		return "orderoverview";
	}


	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/orders/delete")
	public String deleteOrder(@RequestParam Long id) {
		DistributorOrder order = orderRepository.findById(id).orElse(null);

		orderRepository.delete(order);
		return "redirect:/orders";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/orders/complete")
	public String completeOrder(@RequestParam Long id) {
		DistributorOrder order = orderRepository.findById(id).orElse(null);

		order.getItems().forEach(orderItem -> {
			InventoryProduct product = inventoryProductCatalog.findByName(orderItem.getProduct().getName());
			if (product == null) {
				InventoryProduct inventoryProduct = new InventoryProduct(
						distributorProductCatalog.findById(orderItem.getProduct().getId()).get(),
						distributorProductCatalog);

				inventoryProductCatalog.save(inventoryProduct);
				product = inventoryProduct;
			}

			product.setInventoryAmount(product.getInventoryAmount() + orderItem.getQuantity().getAmount().longValue());
			inventoryProductCatalog.save(product);
		});

		orderRepository.delete(order);
		return "redirect:/orders";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/order")
	public String order(Model model,
						@ModelAttribute("cart") OrderCart cart,
						@RequestParam(value = "name", defaultValue = "") String name,
						@RequestParam(value = "amount", defaultValue = "1") Integer amount) {
		Iterable<DistributorProduct> distributorProducts = new ArrayList<>();
		List<DistributorProduct> filtered = new ArrayList<>();


		if (name.length() > 0) {
			((ArrayList<DistributorProduct>) distributorProducts).addAll(distributorProductCatalog.findAll());

			((ArrayList<DistributorProduct>) distributorProducts).removeIf(distributorProduct -> {
				if (!distributorProduct.getName().contains(name)) {
					return true;
				}
				if (distributorProduct.getMinimumOrderAmount() > amount) {
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

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/orderFinished")
	public String completeOrder(Model model,
								@ModelAttribute("cart") OrderCart cart,
								@LoggedIn Optional<UserAccount> userAccount) {

		if (cart.isEmpty()) {
			return "redirect:/order";
		}


		return userAccount.map(account -> {
			Map<Distributor, OrderCart> distributorSetMap = new HashMap<>();
			double total = 0;
			int items = 0;
			for (OrderCartItem cartItem : cart) {
				items++;
				distributorSetMap.computeIfAbsent(
						cartItem.getProduct().getDistributor(),
						distributor -> new OrderCart()).addOrUpdateItem(cartItem.getProduct(),
						cartItem.getQuantity());
				total += cartItem.getPrice().getNumber().doubleValue() * cartItem.getQuantity().getAmount().doubleValue();
			}

			for (Distributor distributor : distributorSetMap.keySet()) {
				DistributorOrder order = new DistributorOrder(account, distributor);
				order.addItems(cart, itemRepository);
				orderRepository.save(order);

			}

			model.addAttribute("totalprice", String.format("%.2f", total));
			model.addAttribute("productcount", items);
			model.addAttribute("distributorcount", distributorSetMap.size());

			return "orderfinished";
		}).orElse("redirect:/login");
	}


	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/order/remove")
	public String removeOrder(Model model, @RequestParam("id") String id, @ModelAttribute("cart") OrderCart cart) {

		cart.removeItem(id);

		return "redirect:/order";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/order/add")
	public String addItem(Model model,
						  @ModelAttribute("cart") OrderCart cart,
						  @RequestParam("id") Long id,
						  @RequestParam("amount") Integer integer) {
		Optional<DistributorProduct> product = distributorProductCatalog.findById(id);

		if (product.isPresent()) {
			cart.addOrUpdateItem(product.get(), integer);
		}

		return "redirect:/order";
	}

	@ModelAttribute("cart")
	OrderCart initializeCart() {
		return new OrderCart();
	}


}
