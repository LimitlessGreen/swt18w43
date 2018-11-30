package bioladen.customer;

import lombok.Getter;
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
		this.discount = discount;
		this.typeName = typeName;
	}
}
