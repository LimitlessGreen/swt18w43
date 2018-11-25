package bioladen.product;

import bioladen.product.distributor_product.DistributorProductCatalog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

	@RequestMapping("/productlist")
	String showProducts(Model model) {
		List<InventoryProduct> inventoryProductList = inventoryProductCatalog.findAll();
		model.addAttribute("inventoryProductList", inventoryProductList);

		return "productlist";
	}

	@GetMapping("/productlist/add")
	String addProduct(@RequestParam("id") Long distributorProductIdentifier) {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(distributorProductIdentifier).get(), distributorProductCatalog);

		inventoryProductCatalog.save(inventoryProduct);

		return "redirect:/productlist";
	}
}
