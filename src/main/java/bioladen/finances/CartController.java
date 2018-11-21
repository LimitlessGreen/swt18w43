package bioladen.finances;

import bioladen.product.ProductCatalog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lukas Petzold
 */
@Controller
@SessionAttributes({"shoppingCart"})

public class CartController {

	private final ProductCatalog productCatalog;


	CartController(ProductCatalog productCatalog) {
		this.productCatalog = productCatalog;
	}


	@ModelAttribute("shoppingCart")
	ShoppingCart initializeShoppingCart() {

		return new ShoppingCart();
	}


	@RequestMapping("/wishlist")
	String wishlist(@ModelAttribute ShoppingCart shoppingCart, Model model) {
		model.addAttribute("shoppingCart", shoppingCart);
		return "wishlist";
	}


	@PostMapping("/addToWishlist")
	String addToWishlist(@RequestParam("productId") String pid, @ModelAttribute ShoppingCart shoppingCart, Model model) {
		shoppingCart.addOrUpdateItem(productCatalog.findById(pid).get(), 1);
		model.addAttribute("shoppingCart", shoppingCart);

		return "productlist";
	}

	@PostMapping("/deleteFromWishlist")
	String deleteFromWishlist(@RequestParam("productId") String pid, @ModelAttribute ShoppingCart shoppingCart, Model model) {
		shoppingCart.removeItem(pid);
		model.addAttribute("shoppingCart", shoppingCart);

		return "wishlist";
	}

	@PostMapping("/addOneToWishlist")
	String addOneToWishlist(@RequestParam("productId") String pid, @ModelAttribute ShoppingCart shoppingCart, Model model) {
		shoppingCart.addOrUpdateItem(shoppingCart.getItem(pid).get().getProduct(), 1);
		model.addAttribute("shoppingCart", shoppingCart);

		return "wishlist";
	}

	@PostMapping("/removeOneFromWishlist")
	String removeOneFromWishlist(@RequestParam("productId") String pid, @ModelAttribute ShoppingCart shoppingCart, Model model) {
		if (shoppingCart.getItem(pid).get().getQuantity() == 1) {
			shoppingCart.removeItem(pid);
		} else {
			shoppingCart.addOrUpdateItem(shoppingCart.getItem(pid).get().getProduct(), -1);
			model.addAttribute("shoppingCart", shoppingCart);
		}

		return "wishlist";
	}

}
