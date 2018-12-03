package bioladen.product;

import lombok.Getter;

public enum Organization {
	BIOLAND("Bioland"),
	BIOPARK("Biopark e.V."),
	DEMETER("Demeter e.V."),
	ECOVIN("ECOVIN Bundesverband Ökologischer Weinbau e.V."),
	EG_BIO("BIO nach EG-Öko-Verordnung"),
	GAEA("Gäa e.V. – Vereinigung ökoloschier Landbau"),
	NATURLAND("Naturland – Verband für ökologischen Landbau e.V.");

	private final @Getter String name;

	Organization(String name) {
		this.name = name;
	}
}
