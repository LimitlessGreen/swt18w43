package bioladen.product.distributor_product;

import bioladen.product.MwStCategory;
import bioladen.product.Organization;
import bioladen.product.ProductCategory;
import bioladen.product.distributor.Distributor;
import bioladen.product.distributor.DistributorRepository;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.quantity.Metric;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DistributorProductDataInitializer implements DataInitializer {
	private final DistributorProductCatalog distributorProductCatalog;
	private final DistributorRepository distributorRepository;

	DistributorProductDataInitializer(DistributorProductCatalog distributorProductCatalog,
									  DistributorRepository distributorRepository) {
		this.distributorProductCatalog = distributorProductCatalog;
		this.distributorRepository = distributorRepository;
	}

	private static final Distributor DISTRIBUTOR1 = new Distributor(
														"Bauer Heinze",
														"heinze@bauern.de",
														"Heinz Heinze",
														"01524506154"
													);
	private static final Distributor DISTRIBUTOR2 = new Distributor(
														"Fridas Hof",
														"frida@fridas-hof.de",
														"Frida Fritzsche",
														"01736825268"
													);

	private static final DistributorProduct DISTRIBUTOR_PRODUCT1 = new DistributorProduct(
																		"Kartoffeln",
																		DISTRIBUTOR1,
																		BigDecimal.valueOf(4.99),
																		BigDecimal.valueOf(5),
																		Metric.KILOGRAM,
																		10,
																		ProductCategory.FOOD_FRUIT_VEG,
																		MwStCategory.REDUCED,
																		null,
																		Organization.BIOLAND
																	);
	private static final DistributorProduct DISTRIBUTOR_PRODUCT2 = new DistributorProduct(
																		"Kartoffeln",
																		DISTRIBUTOR2,
																		BigDecimal.valueOf(4.49),
																		BigDecimal.valueOf(5),
																		Metric.KILOGRAM,
																		15,
																		ProductCategory.FOOD_FRUIT_VEG,
																		MwStCategory.REDUCED,
																		null,
																		Organization.BIOLAND
																	);
	private static final DistributorProduct DISTRIBUTOR_PRODUCT3 = new DistributorProduct(
																		"Kartoffeln",
																		DISTRIBUTOR2,
																		BigDecimal.valueOf(3.50),
																		BigDecimal.valueOf(5),
																		Metric.KILOGRAM,
																		3,
																		ProductCategory.FOOD_FRUIT_VEG,
																		MwStCategory.REDUCED,
																		null,
																		Organization.BIOLAND
																	);

	private static final DistributorProduct DISTRIBUTOR_PRODUCT4 = new DistributorProduct(
																		"Kakao",
																		DISTRIBUTOR2,
																		BigDecimal.valueOf(1.50),
																		BigDecimal.valueOf(1),
																		Metric.KILOGRAM,
																		4,
																		ProductCategory.FOOD_FRUIT_VEG,
																		MwStCategory.REDUCED,
																		null,
																		Organization.BIOLAND
																	);
	private static final DistributorProduct DISTRIBUTOR_PRODUCT5 = new DistributorProduct(
																		"Kakao",
																		DISTRIBUTOR1,
																		BigDecimal.valueOf(59.00),
																		BigDecimal.valueOf(1),
																		Metric.KILOGRAM,
																		4,
																		ProductCategory.FOOD_FRUIT_VEG,
																		MwStCategory.REDUCED,
																		null,
																		Organization.BIOLAND
																	);
	private static final DistributorProduct DISTRIBUTOR_PRODUCT6 = new DistributorProduct(
																		"Milch",
																		DISTRIBUTOR2,
																		BigDecimal.valueOf(1.50),
																		BigDecimal.valueOf(1),
																		Metric.LITER,
																		6,
																		ProductCategory.FOOD_DAIRY,
																		MwStCategory.REDUCED,
																		BigDecimal.valueOf(0.50),
																		Organization.NATURLAND
																	);

	@Override
	public void initialize() {
		distributorRepository.save(DISTRIBUTOR1);
		distributorRepository.save(DISTRIBUTOR2);

		distributorProductCatalog.save(DISTRIBUTOR_PRODUCT1);
		distributorProductCatalog.save(DISTRIBUTOR_PRODUCT2);
		distributorProductCatalog.save(DISTRIBUTOR_PRODUCT3);
		distributorProductCatalog.save(DISTRIBUTOR_PRODUCT4);
		distributorProductCatalog.save(DISTRIBUTOR_PRODUCT5);
		distributorProductCatalog.save(DISTRIBUTOR_PRODUCT6);
	}
}
