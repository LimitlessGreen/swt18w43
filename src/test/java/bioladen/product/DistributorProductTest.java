package bioladen.product;

import bioladen.product.distributor.DistributorRepository;
import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class DistributorProductTest {

	@Autowired
	private DistributorRepository distributorRepository;

	@Autowired
	private DistributorProductCatalog distributorProductCatalog;


	@Test
	void getterMethods() {
		DistributorProduct product = distributorProductCatalog.findById(1L).get();


		assertEquals(1, (long)product.getDistributorProductIdentifier());
		assertEquals("Kartoffeln", product.getName());
		assertEquals(1, (long)product.getDistributor().getId());
		assertEquals(BigDecimal.valueOf(4.99), product.getPrice());
		assertEquals(BigDecimal.valueOf(5).setScale(2, RoundingMode.DOWN), product.getUnit());
		assertEquals(10, product.getMinimumOrderAmount());
		assertEquals(ProductCategory.FOOD_FRUIT_VEG, product.getProductCategory());
		assertEquals(MwStCategory.REDUCED, product.getMwStCategory());
		assertEquals(null, product.getPfandPrice());
		assertEquals(Organization.BIOLAND, product.getOrganization());
	}

}