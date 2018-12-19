package bioladen.order;


import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import bioladen.product.distributor.Distributor;
import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import lombok.Data;
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
	private final OrderManager orderManager;


	/**
	 * Mapping used to display the overview of orders
	 *
	 * @param model Model from Spring
	 * @return Template to be rendered
	 */

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/orders")
	public String orders(Model model) {

		model.addAttribute("orders", orderRepository.findAll());

		return "orderoverview";
	}

	/**
	 * Mapping used to delete orders
	 *
	 * @param id Is the id of the order to be deleted
	 * @return Returns the command to redirect to orders
	 */
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/orders/delete")
	public String deleteOrder(@RequestParam Long id) {
		DistributorOrder order = orderRepository.findById(id).orElse(null);

		orderManager.delete(order);
		return "redirect:/orders";
	}

	/**
	 *  Mapping which gets called when
	 *  the user clicks the button to mark
	 *  a order as finished
	 *  (Inventory Amount gets adjusted)
	 *
	 * @param id Id of the finished order
	 * @return Returns the command to redirect to orders
	 */

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/orders/complete")
	public String completeOrder(@RequestParam Long id) {
		DistributorOrder order = orderRepository.findById(id).orElse(null);


		// Iterate over all Products in this order
		order.getItems().forEach(orderItem -> {
			// find corresponding InventoryProduct or create a new one if there is no one
			InventoryProduct product = inventoryProductCatalog.findByName(orderItem.getProduct().getName());
			if (product == null) {
				InventoryProduct inventoryProduct = new InventoryProduct(
						distributorProductCatalog.findById(orderItem.getProduct().getId()).get(),
						distributorProductCatalog);

				inventoryProductCatalog.save(inventoryProduct);
				product = inventoryProduct;
			}


			// increment InventoryAmount and save it afterwards
			product.setInventoryAmount(product.getInventoryAmount() + orderItem.getQuantity().getAmount().longValue());
			inventoryProductCatalog.save(product);
		});

		orderManager.delete(order);
		return "redirect:/orders";
	}


	/**
	 * Method to display the order page
	 * Including optional Params to filter the products
	 *
	 * @param model Spring Model
	 * @param cart The cart for this UserSession in which items get stored before the order is completed
	 * @param name Name param to filter products
	 * @param amount Amount param to filter products
	 * @return the template for the order page
	 */
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/order")
	public String order(Model model,
						@ModelAttribute("cart") OrderCart cart,
						@RequestParam(value = "name", defaultValue = "") String name,
						@RequestParam(value = "amount", defaultValue = "1") Integer amount) {

		// all products with searched name and amount over minimumOrderAmount
		List<DistributorProduct> productsOverMinimumOrderAmount = new ArrayList<>();
		// all products with searched name and amount under minimumOrderAmount
		List<DistributorProduct> productsUnderMinimumOrderAmount = new ArrayList<>();

		// all products containing the name Filter
		List<DistributorProduct> list = distributorProductCatalog.findAll()
				.stream()
				.filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
				.collect(Collectors.toList());

		// sorting the list by the min order amount and the products
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

		// pass a function to get the current storage amount for easy access
		model.addAttribute("storage", (Function<DistributorProduct, Long>) distributorProduct -> getInventoryAmount(distributorProduct));

		// split list by the amount
		if (!name.isEmpty()) {
			for (DistributorProduct product : list) {
				if (product.getMinimumOrderAmount() > amount) {
					productsUnderMinimumOrderAmount.add(product);
				} else {
					productsOverMinimumOrderAmount.add(product);
				}
			}
		}


		// Map to store the best price for each product matching the name filter & having minOrderAmount < amount
		Map<String, BigDecimal> bestPriceAmount = new HashMap<>();

		// filling the map
		for (DistributorProduct distributorProduct : productsOverMinimumOrderAmount) {
			bestPriceAmount.compute(distributorProduct.getName(), (s, bigDecimal) -> smaller(bigDecimal, distributorProduct.getPrice()));
		}

		// Map to store the best price for each product matching the name filter by ignoring the name filter
		Map<String, BigDecimal> bestPriceAll = new HashMap<>(bestPriceAmount);

		// filling the map
		for (DistributorProduct distributorProduct : productsUnderMinimumOrderAmount) {
			bestPriceAll.compute(distributorProduct.getName(), (s, bigDecimal) -> smaller(bigDecimal, distributorProduct.getPrice()));
		}

		// function to get the note for each product(e.x. best price)
		model.addAttribute("note", (Function<DistributorProduct, BestPriceInfo>) product -> {
			// check if it is the best price overall or for the current ammount
			if (product.getPrice().equals(bestPriceAll.get(product.getName()))) {
				return new BestPriceInfo("insgesamt bester Preis für " + product.getName(), "color:#666060");
			} else if (product.getPrice().equals(bestPriceAmount.get(product.getName())) && product.getMinimumOrderAmount() <= amount ) {
				return new BestPriceInfo("bester Preis für die gewählte Menge " + product.getName(), "color:#c1c1c1");
			}
			return null;
		});


		// calculate Cart total
		double total = 0;
		for (OrderCartItem orderCartItem : cart) {
			total += orderCartItem.getPrice().getNumber().doubleValue() * orderCartItem.getQuantity().getAmount().doubleValue();
		}


		// pass attributes to Model
		model.addAttribute("amount", amount);
		model.addAttribute("articles", cart.get().count());
		model.addAttribute("totalprice", String.format("%.2f", total));
		model.addAttribute("productsOverMinimumOrderAmount", productsOverMinimumOrderAmount);
		model.addAttribute("productsUnderMinimumOrderAmount", productsUnderMinimumOrderAmount);
		model.addAttribute("list", list);
		model.addAttribute("inventoryProductCatalog", inventoryProductCatalog);
		return "order";
	}


	/**
	 * Class to Store the info about the Note for a Product
	 */
	@Data
	public class BestPriceInfo {


		private String displayName;
		private String color;

		/**
		 * Default constructor
		 *
		 * @param displayName Text to be displayed
		 * @param icon Icon to be displayed
		 */

		public BestPriceInfo(String displayName, String icon) {
			this.displayName = displayName;
			this.color = icon;
		}

	}

	/**
	 *
	 * @param distributorProduct
	 * @return the current InventoryAmount(0 if there is no corresponding product)
	 */
	private long getInventoryAmount(DistributorProduct distributorProduct) {
		InventoryProduct inventoryProduct = inventoryProductCatalog.findByName(distributorProduct.getName());
		return inventoryProduct == null ? 0 : inventoryProduct.getInventoryAmount();
	}

	/**
	 * Compares to Comparables and returns the smaller one
	 *
	 * @param o1 Object 1
	 * @param o2 Object 2
	 * @param <T> Type of the objects
	 * @return The smaller one or null if both are null
	 */
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


	/**
	 * Method getting called when submitting order
	 *
	 * @param model Spring Model
	 * @param cart The cart for this UserSession in which items get stored before the order is completed
	 * @param userAccount The current logged in User Account
	 * @return the confirmation page or redirects to the login if you are not logged in
	 */
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/orderFinished")
	public String completeOrder(Model model,
								@ModelAttribute("cart") OrderCart cart,
								@LoggedIn Optional<UserAccount> userAccount) {

		// here we need to add items first
		if (cart.isEmpty()) {
			return "redirect:/order";
		}

		// Mapping useraccount to have to different actions based on the login state
		return userAccount.map(account -> {
			// User is logged in so we can process the order
			// Creating 2 Maps to store the different Parts/Distributors of the order
			Map<Long, OrderCart> distributorOrders = new HashMap<>();
			Map<Long, Distributor> distributorMap = new HashMap<>();
			double total = 0;
			int items = 0;

			// Iterating over the cart to split them by the distributor
			for (OrderCartItem cartItem : cart) {
				items++;
				// Storing them by the id of the distributor, because otherwise they are not equal
				distributorOrders.computeIfAbsent(
						cartItem.getProduct().getDistributor().getId(),
						distributor -> new OrderCart()).addOrUpdateItem(cartItem.getProduct(),
						cartItem.getQuantity());
				// Store the distributor for the id and calculating the price
				distributorMap.put(cartItem.getProduct().getDistributor().getId(), cartItem.getProduct().getDistributor());
				total += cartItem.getPrice().getNumber().doubleValue() * cartItem.getQuantity().getAmount().doubleValue();

			}

			// Creating a order for every distributor
			for (Long distributor : distributorOrders.keySet()) {
				DistributorOrder order = new DistributorOrder(account, distributorMap.get(distributor));
				order.addItems(distributorOrders.get(distributor), orderManager);
				orderManager.createOrder(order);

			}

			// Adding the model attributes
			model.addAttribute("totalprice", String.format("%.2f", total));
			model.addAttribute("productcount", items);
			model.addAttribute("distributorcount", distributorOrders.size());

			cart.clear();

			return "orderfinished";
		}).orElse("redirect:/login");
	}


	/**
	 * Mapping called when the user wants to remove a item from the cart
	 *
	 * @param model Spring Model
	 * @param id Id of the cartitem to be removed
	 * @param cart The cart for this UserSession in which items get stored before the order is completed
	 * @return The redirect action for Spring
	 */

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/order/remove")
	public String removeOrder(Model model, @RequestParam("id") String id, @ModelAttribute("cart") OrderCart cart) {

		cart.removeItem(id);

		return "redirect:/order";
	}


	/**
	 * Mapping called when the user want add a item to the cart
	 *
	 * @param model Spring Model
	 * @param cart The cart for this UserSession in which items get stored before the order is completed
	 * @param id Id of the distributorproduct to be added
	 * @param integer Amount
	 * @return The redirect action for Spring
	 */
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


	/**
	 * Method used by Spring to initialize a new Cart
	 * @return The cart for this UserSession in which items get stored before the order is completed
	 */
	@ModelAttribute("cart")
	OrderCart initializeCart() {
		return new OrderCart();
	}


}
