package bioladen.customer;

import lombok.Getter;

public enum Sex {
	MALE(
			"männlich"
	),
	FEMALE(
			"weiblich"
	),
	VARIOUS(
			"divers"
	);

	private final @Getter String sexName;

	Sex(String sexName){
		this.sexName = sexName;
	}
}
