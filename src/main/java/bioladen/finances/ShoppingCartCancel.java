package bioladen.finances;

import bioladen.datahistory.DataHistoryRequest;
import bioladen.datahistory.EntityLevel;

import java.util.LinkedHashMap;

public class ShoppingCartCancel extends ShoppingCart{

	public ShoppingCart shoppingCart;

	ShoppingCartCancel(ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
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
