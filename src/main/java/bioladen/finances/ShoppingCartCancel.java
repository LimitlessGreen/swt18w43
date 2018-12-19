package bioladen.finances;

import bioladen.customer.Customer;
import bioladen.datahistory.DataHistoryRequest;
import bioladen.datahistory.EntityLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

/**
 * A subclass of the ShoppingCart to make it easier for the statistics to distinguish between a sale and a cancellation.
 *
 * @author Lukas Petzold
 */
@NoArgsConstructor
public class ShoppingCartCancel extends ShoppingCart{

	private @Getter Customer customer;
	private @Getter int amountOfItems;

	ShoppingCartCancel(Customer customer, int amountOfItems) {
		this.customer = customer;
		this.amountOfItems = amountOfItems;
	}

	@Override
	public LinkedHashMap<String, DataHistoryRequest> defineCharts() {
		LinkedHashMap<String, DataHistoryRequest> output = new LinkedHashMap<>();

		output.put("Anzahl Stornierungen", new DataHistoryRequest(this.getClass(), EntityLevel.DELETED));
		return output;
	}

	@Override
	public Double sumUp(String chartName, Double currentValue) {
		switch (chartName) {
			case "Anzahl Stornierungen":
				return currentValue + 1D;
			default:
				return currentValue + 1D;
		}
	}
}
