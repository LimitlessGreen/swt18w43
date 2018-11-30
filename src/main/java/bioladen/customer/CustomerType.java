package bioladen.customer;

import lombok.Getter;
public enum CustomerType {
	HOUSE_CUSTOMER(
			0.05
	),
	MAJOR_CUSTOMER(
			0.10
	),
	STAFF(
			0.15
	),
	MANAGER(
			0.20
	);

	private final @Getter Double discount;

	CustomerType(Double discount){
		this.discount = discount;
	}
}
