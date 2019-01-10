package bioladen.product.stock_taking;

import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StockTakingController {

	private final StockTaking stockTaking;

	private final InventoryProductCatalog inventoryProductCatalog;

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@PostMapping("/product/inventoryStockTaking")
	String inventoryStockTaking(@RequestParam("id")            long id,
								@RequestParam("countedAmount") long amount) {
		stockTaking.registerInventoryAmount(inventoryProductCatalog.findById(id).orElse(null), amount);

		return "redirect:/productlist";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@PostMapping("/product/displayedStockTaking")
	String displayedStockTaking(@RequestParam("id")            long id,
								@RequestParam("countedAmount") long amount) {
		stockTaking.registerDisplayedAmount(inventoryProductCatalog.findById(id).orElse(null), amount);

		return "redirect:/productlist";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@RequestMapping("/stockTaking/begin")
	String beginStockTaking() {
		stockTaking.beginStockTaking();

		return "redirect:/productlist";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@RequestMapping("/stockTaking/finish")
	String finishStockTaking() {
		stockTaking.finishStockTaking();

		for (InventoryProduct ip : inventoryProductCatalog.findAll()) {
			if (stockTaking.getCountedInventoryAmount().containsKey(ip)) {
				ip.setInventoryAmount(stockTaking.getCountedInventoryAmount().get(ip));
			}
			if (stockTaking.getCountedDisplayedAmount().containsKey(ip)) {
				ip.setDisplayedAmount(stockTaking.getCountedDisplayedAmount().get(ip));
			}
			inventoryProductCatalog.save(ip);
		}

		return "redirect:/productlist";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@RequestMapping("/stockTaking")
	String stockTakingTable(Model model) {
		Map<InventoryProduct, String[]> stockTakingTable = new HashMap<>();

		Map<InventoryProduct, Long> countedInventoryAmount = stockTaking.getCountedInventoryAmount();
		Map<InventoryProduct, Long> countedDisplayedAmount = stockTaking.getCountedDisplayedAmount();

		for (Map.Entry<InventoryProduct, Long> entry : countedInventoryAmount.entrySet()) {
			String[] countedAmounts = {"", ""};

			countedAmounts[0] = Long.toString(entry.getValue());
			if (countedDisplayedAmount.containsKey(entry.getKey())) {
				countedAmounts[1] = Long.toString(countedDisplayedAmount.get(entry.getKey()));
			}

			stockTakingTable.put(entry.getKey(), countedAmounts);
		}

		for (Map.Entry<InventoryProduct, Long> entry : countedDisplayedAmount.entrySet()) {
			String[] countedAmounts = {"", ""};

			if (!stockTakingTable.containsKey(entry.getKey())) {
				if (countedInventoryAmount.containsKey(entry.getKey())) {
					countedAmounts[0] = Long.toString(countedInventoryAmount.get(entry.getKey()));
				}
				countedAmounts[1] = Long.toString(entry.getValue());
				stockTakingTable.put(entry.getKey(), countedAmounts);
			}
		}

		model.addAttribute("stockTakingTable", stockTakingTable);
		model.addAttribute("onGoing", stockTaking.isOnGoing());

		return "stockTaking";
	}
}
