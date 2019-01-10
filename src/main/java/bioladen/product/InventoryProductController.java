package bioladen.product;

import bioladen.datahistory.DataHistoryManager;
import bioladen.datahistory.EntityLevel;
import bioladen.product.distributor_product.DistributorProductCatalog;
import bioladen.product.label.PdfLabelGenerator;
import bioladen.product.stock_taking.StockTaking;
import lombok.RequiredArgsConstructor;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class InventoryProductController {
	private final InventoryProductCatalog inventoryProductCatalog;
	private final DistributorProductCatalog distributorProductCatalog;
	private final AuthenticationManager authenticationManager;
	private final StockTaking stockTaking;

	private static final BigDecimal PERCENT_TO_DECIMAL = BigDecimal.valueOf(1.0/100);

	@RequestMapping("/productlist")
	String showProducts(Model model) {
		List<InventoryProduct> inventoryProductList = inventoryProductCatalog.findAll();
		model.addAttribute("inventoryProductList", inventoryProductList);

		model.addAttribute("stockTaking", stockTaking);

		return "productlist";
	}

	@GetMapping("/product")
	String productInfo(@RequestParam Long id, Model model) {
		model.addAttribute("product", inventoryProductCatalog.findById(id).get());
		return "productpage";
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
	public void downloadLabel(HttpServletResponse response, @RequestParam(value = "id") long id) throws IOException {
		PdfLabelGenerator pdfLabelGenerator = new PdfLabelGenerator(inventoryProductCatalog);
		response.setContentType(MediaType.APPLICATION_PDF.toString());
		pdfLabelGenerator.generate(id, response.getOutputStream());
	}

	@GetMapping("/productlist/labels")
	public void downloadAllLabel(HttpServletResponse response) throws IOException {
		PdfLabelGenerator pdfLabelGenerator = new PdfLabelGenerator(inventoryProductCatalog);
		response.setContentType(MediaType.APPLICATION_PDF.toString());
		pdfLabelGenerator.generateAll(inventoryProductCatalog.findAll(), response.getOutputStream());
	}

	@PostMapping("/product/move")
	public String moveAmount(
			@RequestParam("id")			long inventoryProductId,
			@RequestParam("moveAmount") long moveAmount,
			Model model) {
		InventoryProduct inventoryProduct = inventoryProductCatalog.findById(inventoryProductId).orElse(null);

		boolean success = inventoryProduct != null && inventoryProduct.moveAmountFromInventoryToDisplay(moveAmount);

		if (success) {
			publishEvent(inventoryProductCatalog.save(inventoryProduct), EntityLevel.MODIFIED);
		}

		if (!success) {
			model.addAttribute("error", "Ungültige Eingabe");
		}

		return "redirect:/productlist";
	}

	@PostMapping("/setProfitMargin")
	public String setProfitMargin(@RequestParam("profitMargin") BigDecimal profitMarginPercent) {
		InventoryProduct.setProfitMargin(profitMarginPercent.multiply(PERCENT_TO_DECIMAL));

		for (InventoryProduct ip : inventoryProductCatalog.findAll()) {
			ip.calculatePrice();
			inventoryProductCatalog.save(ip);
		}

		return "redirect:/admin";
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
	private final DataHistoryManager<InventoryProduct> dataHistoryManager;

	private void publishEvent(InventoryProduct inventoryProduct, EntityLevel entityLevel) {
		Optional<UserAccount> currentUser = this.authenticationManager.getCurrentUser();
		dataHistoryManager.push(
				inventoryProduct.getName(),
				inventoryProduct,
				entityLevel,
				currentUser.orElse(null));
	}
}
