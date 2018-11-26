package bioladen.product.distributor_product;

import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import bioladen.product.distributor.Distributor;
import bioladen.product.distributor.DistributorRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class DistributorProductController implements ApplicationEventPublisherAware {

	private final DistributorProductCatalog distributorProductCatalog;
	private final DistributorRepository distributorRepository;

	DistributorProductController(
			DistributorProductCatalog distributorProductCatalog,
			DistributorRepository distributorRepository) {
		this.distributorProductCatalog = distributorProductCatalog;
		this.distributorRepository = distributorRepository;
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@GetMapping("/distributorproductlist")
	String showDistributorProducts(Model model) {
		List<DistributorProduct> distributorProductList = distributorProductCatalog.findAll();
		List<Distributor> distributorList = distributorRepository.findAll();

		model.addAttribute("distributorProductList", distributorProductList);
		model.addAttribute("distributorList", distributorList);

		return "distributorproductlist";
	}

	@RequestMapping("/addDistributorProduct")
	String distributorForm(Model model) {
		List<Distributor> distributorList = distributorRepository.findAll();
		model.addAttribute("distributorList", distributorList);

		return "distributorproductform";
	}

	@PostMapping("/addDistributorProduct")
	String addDistributor(@RequestParam("name")	              String name,
						  @RequestParam("distributor")        Long   distributorId,
						  @RequestParam("price")              String priceString,
						  @RequestParam("unit")               String unitString,
						  @RequestParam("minimumOrderAmount") long   minimumOrderAmount) {

		BigDecimal price = new BigDecimal(priceString);
		BigDecimal unit = new BigDecimal(unitString);
		Distributor distributor = distributorRepository.findById(distributorId).get();

		DistributorProduct distributorProduct = new DistributorProduct(name,
				                                                       distributor,
				                                                       price,
				                                                       unit,
				                                                       minimumOrderAmount);
		distributorProductCatalog.save(distributorProduct);

		// (👁 ᴥ 👁) Event
		publishEvent(distributorProduct, EntityLevel.CREATED);

		return "redirect:/distributorproductlist";
	}

//	@PostMapping("/removeDistributor")
//	String removeDistributor(@RequestParam("distributorIdentifier") String id) {
//		distributorRepository.deleteById(id);
//
//		return "redirect:/distributorlist";
//	}

	/* TODO: Event for distributor product deletions
	 _________________
	< Event publisher >
	 -----------------
        \   ^__^
         \  (@@)\_______
            (__)\       )\/\
                ||----w |
                ||     ||

	*/
	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	private void publishEvent(DistributorProduct distributorProduct, EntityLevel entityLevel) {
		publisher.publishEvent(new EntityEvent<>(distributorProduct, entityLevel));
	}
}
