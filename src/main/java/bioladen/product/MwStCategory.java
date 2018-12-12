package bioladen.product;

import lombok.Getter;

public enum MwStCategory {
	REGULAR("A", 0.19),
	REDUCED("B", 0.07),
	EXEMPT("X", 0.00);

	private final @Getter String name;
	private final @Getter double percentage;

	MwStCategory(String name, double percentage) {
		this.name = name;
		this.percentage = percentage;
	}


	public String getLongName() {
		final int TO_PERCENT = 100;

		return this.name + " (" + Math.round(this.percentage * TO_PERCENT) + " %)";
	}
}
