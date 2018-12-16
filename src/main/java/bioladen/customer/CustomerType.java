package bioladen.customer;

import lombok.Getter;

/**
 * Customer Types with German translation and discount
 * @author Lisa Riedel
 */
public enum CustomerType {
	HOUSE_CUSTOMER(
			"Hauskunde",
			0.05
	),
	MAJOR_CUSTOMER(
			"Großkunde",
			0.10
	),
	STAFF(
			"Personal",
			0.15
	),
	MANAGER(
			"Manager",
			0.20
	);

	private final @Getter String typeName;
	private final @Getter Double discount;

	CustomerType(String typeName, Double discount){
		this.typeName = typeName;
		this.discount = discount;
	}
}
