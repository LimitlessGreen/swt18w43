package bioladen.finances;

import bioladen.datahistory.DataHistoryRequest;
import bioladen.datahistory.EntityLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@NoArgsConstructor
public class ShoppingCartSale extends ShoppingCart {

	public ShoppingCart shoppingCart;

	public ShoppingCartSale(ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}

	@Override
	public LinkedHashMap<String, DataHistoryRequest> defineCharts() {
		LinkedHashMap<String, DataHistoryRequest> output = new LinkedHashMap<>();

		output.put("Ausgaben (Pfand)", new DataHistoryRequest(this.getClass(), EntityLevel.CREATED));
		output.put("Ausgaben (Steuer)", new DataHistoryRequest(this.getClass(), EntityLevel.CREATED));
		return output;
	}

	@Override
	public Double sumUp(String chartName, Double currentValue) {

		switch (chartName) {
			case "Ausgaben (Pfand)":
				return currentValue + this.getMwstMoney().doubleValue();
			case "Ausgaben (Steuer)":
				return currentValue + this.getMwstMoney().doubleValue();
			default:
				return currentValue + 1D;
		}
	}
}
