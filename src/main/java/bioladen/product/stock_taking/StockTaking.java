package bioladen.product.stock_taking;

import bioladen.product.InventoryProduct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockTaking {
	private @Getter @Setter boolean onGoing = false;

	private @Getter @Setter Map<InventoryProduct, Long> countedInventoryAmount = new HashMap<>();
	private @Getter @Setter Map<InventoryProduct, Long> countedDisplayedAmount = new HashMap<>();

	/**
	 * Starts the stock taking process. Clears all old counted amounts.
	 */
	public void beginStockTaking() {
		if (!this.onGoing) {
			this.onGoing = true;

			this.countedInventoryAmount.clear();
			this.countedDisplayedAmount.clear();
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * Finishes the stock taking process. The counted amounts remain in the lists.
	 */
	public void finishStockTaking() {
		if (this.onGoing) {
			this.onGoing = false;
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * Registers the Counted Inventory Amount.
	 *
	 * @param inventoryProduct Product that was counted.
	 * @param amount Inventory amount.
	 */
	public void registerInventoryAmount(InventoryProduct inventoryProduct, long amount) {
		countedInventoryAmount.put(inventoryProduct, amount);
	}

	/**
	 * Registers the Counted Displayed Amount.
	 *
	 * @param inventoryProduct Product that was counted.
	 * @param amount Counted amount.
	 */
	public void registerDisplayedAmount(InventoryProduct inventoryProduct, long amount) {
		countedDisplayedAmount.put(inventoryProduct, amount);
	}
}
