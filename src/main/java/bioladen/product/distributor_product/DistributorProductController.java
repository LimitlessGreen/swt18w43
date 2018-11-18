package bioladen.product.distributor_product;

import bioladen.product.distributor.Distributor;
import bioladen.product.distributor.DistributorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

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
		List<Distributor> distributorList = distributorRepository.findAll();
		model.addAttribute("distributorList", distributorList);

		return "distributorproductform";
	}

	@PostMapping("/addDistributorProduct")
	String addDistributor(@RequestParam("name")	              String      name,
						  @RequestParam("distributor")        Distributor distributor,
						  @RequestParam("price")              String      priceString,
						  @RequestParam("unit")               String      unitString,
						  @RequestParam("minimumOrderAmount") long        minimumOrderAmount) {

		BigDecimal price = new BigDecimal(priceString);
		BigDecimal unit = new BigDecimal(unitString);

		DistributorProduct distributorProduct = new DistributorProduct(name,
				                                                       distributor,
				                                                       price,
				                                                       unit,
				                                                       minimumOrderAmount);
		distributorProductCatalog.save(distributorProduct);

		return "distributorproductform";
	}

//	@PostMapping("/removeDistributor")
//	String removeDistributor(@RequestParam("distributorIdentifier") String id) {
//		distributorRepository.deleteById(id);
//
//		return "redirect:/distributorlist";
//	}
}
