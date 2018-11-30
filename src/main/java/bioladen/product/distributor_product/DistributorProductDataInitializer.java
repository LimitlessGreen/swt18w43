package bioladen.product.distributor_product;

import bioladen.product.ProductCategory;
import bioladen.product.distributor.DistributorRepository;
import org.salespointframework.core.DataInitializer;
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
		distributorProductCatalog.save(
				new DistributorProduct(
						"Kartoffeln",
						distributorRepository.findById(1L).get(),
						BigDecimal.valueOf(4.99),
						BigDecimal.valueOf(5), // KG
						10,
						ProductCategory.FOOD_FRUIT_VEG,
						null
				)
		);

		distributorProductCatalog.save(
				new DistributorProduct(
						"Kartoffeln",
						distributorRepository.findById(2L).get(),
						BigDecimal.valueOf(4.49),
						BigDecimal.valueOf(5), // KG
						15,
						ProductCategory.FOOD_FRUIT_VEG,
						null
				)
		);

		distributorProductCatalog.save(
				new DistributorProduct(
						"Milch",
						distributorRepository.findById(2L).get(),
						BigDecimal.valueOf(1.50),
						BigDecimal.valueOf(1), // L
						6,
						ProductCategory.FOOD_DAIRY,
						BigDecimal.valueOf(0.50)
				)
		);
	}
}
