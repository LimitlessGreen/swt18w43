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

	@Override
	public void initialize() {
		distributorRepository.save(
				new Distributor(
						"Bauer Heinze",
						"heinze@bauern.de",
						"Heinz Heinze",
						"01524506154"
				)
		);

		distributorRepository.save(
				new Distributor(
						"Fridas Hof",
						"frida@fridas-hof.de",
						"Frida Fritzsche",
						"01736825268"
				)
		);

		distributorProductCatalog.save(
				new DistributorProduct(
						"Kartoffeln",
						distributorRepository.findById(1L).get(),
						BigDecimal.valueOf(4.99),
						BigDecimal.valueOf(5),
						Metric.KILOGRAM,
						10,
						ProductCategory.FOOD_FRUIT_VEG,
						MwStCategory.REDUCED,
						null,
						Organization.BIOLAND
				)
		);

		distributorProductCatalog.save(
				new DistributorProduct(
						"Kartoffeln",
						distributorRepository.findById(2L).get(),
						BigDecimal.valueOf(4.49),
						BigDecimal.valueOf(5),
						Metric.KILOGRAM,
						15,
						ProductCategory.FOOD_FRUIT_VEG,
						MwStCategory.REDUCED,
						null,
						Organization.BIOLAND
				)
		);

		distributorProductCatalog.save(
				new DistributorProduct(
						"Kartoffeln",
						distributorRepository.findById(2L).get(),
						BigDecimal.valueOf(3.50),
						BigDecimal.valueOf(5),
						Metric.KILOGRAM,
						3,
						ProductCategory.FOOD_FRUIT_VEG,
						MwStCategory.REDUCED,
						null,
						Organization.BIOLAND
				)
		);

		distributorProductCatalog.save(
				new DistributorProduct(
						"Kakao",
						distributorRepository.findById(2L).get(),
						BigDecimal.valueOf(1.50),
						BigDecimal.valueOf(1),
						Metric.KILOGRAM,
						4,
						ProductCategory.FOOD_FRUIT_VEG,
						MwStCategory.REDUCED,
						null,
						Organization.BIOLAND
				)
		);

		distributorProductCatalog.save(
				new DistributorProduct(
						"Kakao",
						distributorRepository.findById(1L).get(),
						BigDecimal.valueOf(59.00),
						BigDecimal.valueOf(1),
						Metric.KILOGRAM,
						4,
						ProductCategory.FOOD_FRUIT_VEG,
						MwStCategory.REDUCED,
						null,
						Organization.BIOLAND
				)
		);

		distributorProductCatalog.save(
				new DistributorProduct(
						"Milch",
						distributorRepository.findById(2L).get(),
						BigDecimal.valueOf(1.50),
						BigDecimal.valueOf(1),
						Metric.LITER,
						6,
						ProductCategory.FOOD_DAIRY,
						MwStCategory.REDUCED,
						BigDecimal.valueOf(0.50),
						Organization.NATURLAND
				)
		);
	}
}
