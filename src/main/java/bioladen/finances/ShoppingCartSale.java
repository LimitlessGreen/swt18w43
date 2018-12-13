package bioladen.finances;

import bioladen.customer.Customer;
import bioladen.datahistory.DataHistoryRequest;
import bioladen.datahistory.EntityLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@NoArgsConstructor
public class ShoppingCartSale extends ShoppingCart {

	private @Getter	Customer customer;
	private @Getter BigDecimal pfandMoney;
	private @Getter BigDecimal mwstMoney;
	private @Getter BigDecimal saleMoney;

	public ShoppingCartSale(Customer customer, BigDecimal pfandMoney, BigDecimal mwstMoney, BigDecimal saleMoney) {
		this.customer = customer;
		this.pfandMoney = pfandMoney;
		this.mwstMoney = mwstMoney;
		this.saleMoney = saleMoney;
	}


	@Override
	public LinkedHashMap<String, DataHistoryRequest> defineCharts() {
		LinkedHashMap<String, DataHistoryRequest> output = new LinkedHashMap<>();

		output.put("Ausgaben (Pfand)", new DataHistoryRequest(this.getClass(), EntityLevel.CREATED));
		output.put("Ausgaben (Steuer)", new DataHistoryRequest(this.getClass(), EntityLevel.CREATED));
		output.put("Einnahmen (Umsatz)", new DataHistoryRequest(this.getClass(), EntityLevel.CREATED));
		return output;
	}

	@Override
	public Double sumUp(String chartName, Double currentValue) {

		switch (chartName) {
			case "Ausgaben (Pfand)":
				return currentValue + this.pfandMoney.doubleValue();
			case "Ausgaben (Steuer)":
				return currentValue + this.mwstMoney.doubleValue();
			case "Einnahmen (Umsatz)":
				return currentValue + this.saleMoney.doubleValue();
			default:
				return currentValue + 1D;
		}
	}
}
