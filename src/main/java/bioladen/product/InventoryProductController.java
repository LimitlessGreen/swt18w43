package bioladen.product;

import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import bioladen.product.distributor_product.DistributorProductCatalog;
import bioladen.product.label.PdfLabelGenerator;
import lombok.RequiredArgsConstructor;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
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

	@GetMapping("/product/label")
	public HttpEntity<Resource> download(@RequestParam(value = "id") long id) {

		PdfLabelGenerator pdfLabelGenerator = new PdfLabelGenerator();
		pdfLabelGenerator.generate(inventoryProductCatalog.findById(id).orElse(null));

		File file = new File("src/main/resources/generated/p" + id + ".pdf");

		ContentDisposition disposition = ContentDisposition //
				.builder("inline") //
				.filename(file.getName()) //
				.build();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDisposition(disposition);
		headers.setContentLength(file.length());

		return ResponseEntity.ok() //
				.headers(headers) //
				.body(new FileSystemResource(file));
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
