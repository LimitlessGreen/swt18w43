package bioladen.product.stock_taking;

import bioladen.product.InventoryProductCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class StockTakingController {

	private final StockTaking stockTaking;

	private final InventoryProductCatalog inventoryProductCatalog;

	@GetMapping("/product/inventoryStockTaking")
	String inventoryStockTaking(@RequestParam("id")     long id,
								@RequestParam("amount") long amount) {
		stockTaking.registerInventoryAmount(inventoryProductCatalog.findById(id).orElse(null), amount);

		return "redirect:/productlist";
	}

	@GetMapping("/product/displayedStockTaking")
	String displayedStockTaking(@RequestParam("id")     long id,
								@RequestParam("amount") long amount) {
		stockTaking.registerDisplayedAmount(inventoryProductCatalog.findById(id).orElse(null), amount);

		return "redirect:/productlist";
	}
}
