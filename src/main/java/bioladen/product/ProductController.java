package bioladen.product;

import bioladen.product.distributor.Distributor;
import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {
	private final ProductCatalog productCatalog;
	private final DistributorProductCatalog distributorProductCatalog;

	ProductController(ProductCatalog productCatalog, DistributorProductCatalog distributorProductCatalog) {
		this.productCatalog = productCatalog;
		this.distributorProductCatalog = distributorProductCatalog;
	}

	@RequestMapping("/products")
	String showProducts(Model model) {
		List<Product> productList = productCatalog.findAll();
		model.addAttribute("productList", productList);

		return "productlist";
	}

	@PostMapping("/addProduct")
	String addProduct(@RequestParam("distributorProductIdentifier") String distributorProductIdentifier) {
		Product product = new Product(distributorProductCatalog.findById(distributorProductIdentifier).get().getName(), distributorProductCatalog);

		productCatalog.save(product);

		return "redirect:/products";
	}
}
