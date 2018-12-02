package bioladen.product;

import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import bioladen.product.distributor_product.DistributorProductCatalog;
import lombok.RequiredArgsConstructor;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class InventoryProductController implements ApplicationEventPublisherAware {
	private final InventoryProductCatalog inventoryProductCatalog;
	private final DistributorProductCatalog distributorProductCatalog;
	private final AuthenticationManager authenticationManager;

	@RequestMapping("/productlist")
	String showProducts(Model model) {
		List<InventoryProduct> inventoryProductList = inventoryProductCatalog.findAll();
		model.addAttribute("inventoryProductList", inventoryProductList);

		return "productlist";
	}

	@GetMapping("/productlist/add")
	String addProduct(@RequestParam("id") Long distributorProductIdentifier) {
		InventoryProduct inventoryProduct = new InventoryProduct(
				distributorProductCatalog.findById(distributorProductIdentifier).get(),
				distributorProductCatalog);

		inventoryProductCatalog.save(inventoryProduct);

		// (👁 ᴥ 👁) Event
		publishEvent(inventoryProduct, EntityLevel.CREATED);

		return "redirect:/productlist";
	}

	/* TODO: Event for inventory product deletions
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

	private void publishEvent(InventoryProduct inventoryProduct, EntityLevel entityLevel) {
		Optional<UserAccount> currentUser = this.authenticationManager.getCurrentUser();
		publisher.publishEvent(new EntityEvent<>(
				inventoryProduct,
				entityLevel,
				currentUser.orElse(null)));
	}
}
