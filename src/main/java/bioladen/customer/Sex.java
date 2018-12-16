package bioladen.customer;

import lombok.Getter;

/**
 * Customer sex with translation
 * @author Lisa Riedel
 */

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
