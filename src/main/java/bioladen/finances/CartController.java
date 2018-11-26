package bioladen.finances;

import bioladen.product.InventoryProductCatalog;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lukas Petzold
 */
@Controller
@SessionAttributes({"shoppingCart"})

public class CartController {

	private final InventoryProductCatalog inventoryProductCatalog;


	CartController(InventoryProductCatalog inventoryProductCatalog) {
		this.inventoryProductCatalog = inventoryProductCatalog;
	}


	@ModelAttribute("shoppingCart")
	ShoppingCart initializeShoppingCart() {

		return new ShoppingCart();
	}

	@PreAuthorize("!(hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF'))")
	@RequestMapping("/wishlist")
	String wishlist(@ModelAttribute ShoppingCart shoppingCart, Model model) {
		model.addAttribute("shoppingCart", shoppingCart);
		return "wishlist";
	}


	/**
	 * Add items with the
	 * @param pid to the
	 * @param shoppingCart
	 */
	@GetMapping("/addToWishlist")
	String addToWishlist(@RequestParam("productId") Long pid, @ModelAttribute ShoppingCart shoppingCart, Model model) {
		shoppingCart.addOrUpdateItem(inventoryProductCatalog.findById(pid).get(), 1);
		model.addAttribute("shoppingCart", shoppingCart);

		return "redirect:/productlist";
	}


	/**
	 * Deletes items with the
	 * @param pid from the
	 * @param shoppingCart
	 */
	@PostMapping("/deleteFromWishlist")
	String deleteFromWishlist(
			@RequestParam("productId") String pid,
			@ModelAttribute ShoppingCart shoppingCart,
			Model model) {
		shoppingCart.removeItem(pid);
		model.addAttribute("shoppingCart", shoppingCart);

		return "wishlist";
	}


	/**
	 * Adds one quantity of the item with the
	 * @param pid to the
	 * @param shoppingCart
	 */
	@PostMapping("/addOneToWishlist")
	String addOneToWishlist(
			@RequestParam("productId") String pid,
			@ModelAttribute ShoppingCart shoppingCart,
			Model model) {
		shoppingCart.addOrUpdateItem(shoppingCart.getItem(pid).get().getInventoryProduct(), 1);
		model.addAttribute("shoppingCart", shoppingCart);

		return "wishlist";
	}


	/**
	 * Removes one item with the
	 * @param pid from the
	 * @param shoppingCart
	 */
	@PostMapping("/removeOneFromWishlist")
	String removeOneFromWishlist(
			@RequestParam("productId") String pid,
			@ModelAttribute ShoppingCart shoppingCart,
			Model model) {
		if (shoppingCart.getItem(pid).get().getQuantity() == 1) {
			shoppingCart.removeItem(pid);
		} else {
			shoppingCart.addOrUpdateItem(shoppingCart.getItem(pid).get().getInventoryProduct(), -1);
			model.addAttribute("shoppingCart", shoppingCart);
		}

		return "wishlist";
	}

}
