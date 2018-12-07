package bioladen.product;

import lombok.Getter;

public enum ProductCategory {
	// https://www.fsai.ie/assets/0/86/204/d7af0602-b3e1-4788-802a-0684b3199e91.pdf

	FOOD_DAIRY(
			"Dairy Products",
			"Milchprodukte"
	),
	FOOD_EGG(
			"Egg and Egg Products",
			"Ei und Eiprodukte"
	),
	FOOD_MEAT(
			"Meat and Meat Products, Game and Poultry",
			"Fleisch und Fleischprodukte, Wild and Geflügel"
	),
	FOOD_FISH(
			"Fish, Shellfish and Molluscs",
			"Fisch, Schalentiere, Weichtier"
	),
	FOOD_FAT_OIL(
			"Fats and Oils",
			"Fette und Öle"
	),
	FOOD_SOUP_SAUCE(
			"Soups, Broths and Sauces",
			"Supper, Brühen und Soßen"
	),
	FOOD_BAKERY(
			"Cereals and Bakery Products",
			"Getreide- und Backwaren"
	),
	FOOD_FRUIT_VEG(
			"Fruit and Vegetables",
			"Früchte und Gemüse"
	),
	FOOD_HERB_SPICE(
			"Herbs and Spices",
			"Kräuter und Gewürze"
	),
	FOOD_NON_ALC_BEV(
			"Non-Alcoholic Beverages",
			"Alkoholfreie Getränke"
	),
	FOOD_WINE(
			"Wine",
			"Wein"
	),
	FOOD_ALC_BEV(
			"Alcoholic Beverages (Other than Wine)",
			"Alkoholische Getränke (Außer Wein)"
	),
	FOOD_ICE_DESSERT(
			"Ices and Desserts",
			"Eis- und Süßspeisen"
	),
	FOOD_COCOA_COFFEE_TEA(
			"Cocoa and Cocoa Preparations, Coffee and Tea",
			"Kakao und Kakaozuberetungen, Kaffee und Tee"
	),
	FOOD_CONFECTIONARY(
			"Confectionery",
			"Süßwaren"
	),
	FOOD_NUT_SNACK(
			"Nuts and Nut Products, Snacks",
			"Schalenfrüchte und Nussprodukte, Snacks"
	),
	FOOD_PREPARED(
			"Prepared Dishes",
			"Fertiggerichte"
	),
	FOOD_NUTRITIONAL(
			"Foodstuffs Intended For Special Nutritional Uses",
			"Lebensmittel für besondere Ernährung"
	),
	FOOD_ADDITIVE(
			"Additives",
			"Zusatzstoffe"
	),
	FOOD_MATERIAL(
			"Materials and Articles Intended to come into Contact with Foodstuffs",
			"Materialien und Gegenstände aus Kunststoff, die dazu bestimmt sind, mit Lebensmitteln in Berührung zu kommen"
	),
	FOOD_OTHER(
			"Other Food",
			"Sonstige Lebensmittel"
	);

	private final @Getter String descriptionEn;
	private final @Getter String descriptionDe;

	ProductCategory(String descriptionEn, String descriptionDe) {
		this.descriptionEn = descriptionEn;
		this.descriptionDe = descriptionDe;
	}
}
