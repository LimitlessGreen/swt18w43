package bioladen.product;

import bioladen.product.distributor_product.DistributorProductCatalog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class InventoryProductController {
	private final InventoryProductCatalog inventoryProductCatalog;
	private final DistributorProductCatalog distributorProductCatalog;

	InventoryProductController(InventoryProductCatalog inventoryProductCatalog, DistributorProductCatalog distributorProductCatalog) {
		this.inventoryProductCatalog = inventoryProductCatalog;
		this.distributorProductCatalog = distributorProductCatalog;
	}

	@RequestMapping("/products")
	String showProducts(Model model) {
		List<InventoryProduct> inventoryProductList = inventoryProductCatalog.findAll();
		model.addAttribute("inventoryProductList", inventoryProductList);

		return "productlist";
	}

	@PostMapping("/addProduct")
	String addProduct(@RequestParam("distributorProductIdentifier") Long distributorProductIdentifier) {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(distributorProductIdentifier).get().getName(), distributorProductCatalog);

		inventoryProductCatalog.save(inventoryProduct);

		return "redirect:/products";
	}
}
