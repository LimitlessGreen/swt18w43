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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
		List<DistributorProduct> productsOverMinimumOrderAmount = new ArrayList<>(); //all products with searched name and amount over minimumOrderAmount
		List<DistributorProduct> productsUnderMinimumOrderAmount = new ArrayList<>();  //all products with searched name and amount under minimumOrderAmount

		List<DistributorProduct> list = distributorProductCatalog.findAll().stream().filter(product -> product.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());

		list.sort((o1, o2) -> {
			boolean b1 = o1.getMinimumOrderAmount() > amount;
			boolean b2 = o2.getMinimumOrderAmount() > amount;
			if (b1 == b2) {
				return o1.compareTo(o2);
			} else if (b1) {
				return 1;
			} else {
				return -1;
			}
		});

		model.addAttribute("storage", (Function<DistributorProduct, Long>) distributorProduct -> getInventoryAmount(distributorProduct));

		if (!name.isEmpty()) {
			for (DistributorProduct product : list) {
				if (product.getMinimumOrderAmount() > amount) {
					productsUnderMinimumOrderAmount.add(product);
				} else {
					productsOverMinimumOrderAmount.add(product);
				}
			}
		}


		Map<String, BigDecimal> bestPriceAmount = new HashMap<>();

		for (DistributorProduct distributorProduct : productsOverMinimumOrderAmount) {
			bestPriceAmount.compute(distributorProduct.getName(), (s, bigDecimal) -> smaller(bigDecimal, distributorProduct.getPrice()));
		}

		Map<String, BigDecimal> bestPriceAll = new HashMap<>(bestPriceAmount);

		for (DistributorProduct distributorProduct : productsUnderMinimumOrderAmount) {
			bestPriceAll.compute(distributorProduct.getName(), (s, bigDecimal) -> smaller(bigDecimal, distributorProduct.getPrice()));
		}

		model.addAttribute("note", (Function<DistributorProduct, String>) product -> {
			if (product.getMinimumOrderAmount() > amount) {
				if (product.getPrice().equals(bestPriceAll.get(product.getName()))) {

					return "insgesamt bester Preis für " + product.getName();
				}
			} else {
				if (product.getPrice().equals(bestPriceAmount.get(product.getName()))) {
					if (product.getPrice().equals(bestPriceAll.get(product.getName()))) {
						return "insgesamt bester Preis für " + product.getName();
					}
					return "bester Preis für die gewählte Menge " + product.getName();
				}
			}

			return "";
		});


		double total = 0;
		for (OrderCartItem orderCartItem : cart) {
			total += orderCartItem.getPrice().getNumber().doubleValue() * orderCartItem.getQuantity().getAmount().doubleValue();
		}

		for (DistributorProduct distributorProduct : list) {

		}

		model.addAttribute("amount", amount);
		model.addAttribute("articles", cart.get().count());
		model.addAttribute("totalprice", String.format("%.2f", total));
		model.addAttribute("productsOverMinimumOrderAmount", productsOverMinimumOrderAmount);
		model.addAttribute("productsUnderMinimumOrderAmount", productsUnderMinimumOrderAmount);
		model.addAttribute("list", list);
		model.addAttribute("inventoryProductCatalog", inventoryProductCatalog);
		return "order";
	}

	private long getInventoryAmount(DistributorProduct distributorProduct) {
		InventoryProduct inventoryProduct = inventoryProductCatalog.findByName(distributorProduct.getName());
		return inventoryProduct == null ? 0 : inventoryProduct.getInventoryAmount();
	}

	private <T extends Comparable> T smaller(T o1, T o2) {
		if (o2 == null) {
			return o1;
		}
		if (o1 == null) {
			return o2;
		}
		if (o1.compareTo(o2) == 1) {
			return o2;
		}
		return o1;
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
			Map<Long, OrderCart> distributorOrders = new HashMap<>();
			Map<Long, Distributor> distributorMap = new HashMap<>();
			double total = 0;
			int items = 0;
			for (OrderCartItem cartItem : cart) {
				items++;
				distributorOrders.computeIfAbsent(
						cartItem.getProduct().getDistributor().getId(),
						distributor -> new OrderCart()).addOrUpdateItem(cartItem.getProduct(),
						cartItem.getQuantity());
				distributorMap.put(cartItem.getProduct().getDistributor().getId(), cartItem.getProduct().getDistributor());
				total += cartItem.getPrice().getNumber().doubleValue() * cartItem.getQuantity().getAmount().doubleValue();

			}

			for (Long distributor : distributorOrders.keySet()) {
				DistributorOrder order = new DistributorOrder(account, distributorMap.get(distributor));
				order.addItems(distributorOrders.get(distributor), itemRepository);
				orderRepository.save(order);

			}

			model.addAttribute("totalprice", String.format("%.2f", total));
			model.addAttribute("productcount", items);
			model.addAttribute("distributorcount", distributorOrders.size());

			cart.clear();

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
