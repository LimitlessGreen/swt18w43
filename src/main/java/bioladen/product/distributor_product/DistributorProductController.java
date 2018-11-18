package bioladen.product.distributor_product;

import bioladen.product.distributor.Distributor;
import bioladen.product.distributor.DistributorRepository;
import org.javamoney.moneta.Money;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

import static org.salespointframework.core.Currencies.EURO;

@Controller
public class DistributorProductController {

	private final DistributorProductCatalog distributorProductCatalog;
	private final DistributorRepository distributorRepository;

	DistributorProductController(DistributorProductCatalog distributorProductCatalog, DistributorRepository distributorRepository) {
		this.distributorProductCatalog = distributorProductCatalog;
		this.distributorRepository = distributorRepository;
	}

	@GetMapping("/distributorproductlist")
	String showDistributorProducts(Model model) {
		List<DistributorProduct> distributorProductList = distributorProductCatalog.findAll();
		model.addAttribute("distributorProductList", distributorProductList);

		return "distributorproductlist";
	}

	@RequestMapping("/addDistributorProduct")
	String distributorForm(Model model) {
		return "distributorproductform";
	}

//	@PostMapping("/addDistributorProduct")
//	String addDistributor(@RequestParam("name")	              String      name,
//						  @RequestParam("distributor")        Distributor distributor,
//						  @RequestParam("price")              double      price,
//						  @RequestParam("unit")               double      unit,
//						  @RequestParam("metric")             String      metric,
//						  @RequestParam("minimumOrderAmount") long        minimumOrderAmount) {
//
//		DistributorProduct distributorProduct = new DistributorProduct(name,
//				                                                       distributor,
//				                                                       Money.of(price, EURO),
//				                                                       Quantity.of(unit),
//				                                                       Metric.from(metric),
//				                                                       minimumOrderAmount);
//		distributorProductCatalog.save(distributorProduct);
//
//		return "distributorproductform";
//	}

//	@PostMapping("/removeDistributor")
//	String removeDistributor(@RequestParam("distributorIdentifier") DistributorIdentifier id) {
//		distributorRepository.deleteById(id);
//
//		return "redirect:/distributorlist";
//	}
}
