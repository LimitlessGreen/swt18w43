package bioladen.product;

import bioladen.BioLaden;
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
class InventoryProductTest {

	@Autowired
	private DistributorProductCatalog distributorProductCatalog;

	@Autowired
	private InventoryProductCatalog inventoryProductCatalog;


	@Test
	void addInventoryAmount() {
		InventoryProduct product = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		product.setInventoryAmount(0);

		product.addInventoryAmount(5);

		assertEquals(5, product.getInventoryAmount());
	}

	@Test
	void moveAmountFromInventoryToDisplay() {
		InventoryProduct product = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		product.setInventoryAmount(10);
		product.setDisplayedAmount(0);

		product.moveAmountFromInventoryToDisplay(5);

		assertEquals(5, product.getInventoryAmount());
		assertEquals(5, product.getDisplayedAmount());
	}

	@Test
	void removeDisplayedAmount() {
		InventoryProduct product = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		product.setDisplayedAmount(10);

		product.removeDisplayedAmount(5);

		assertEquals(5, product.getDisplayedAmount());
	}

	@Test
	void toEan13FromEan13() {
		long id = InventoryProduct.fromEan13(InventoryProduct.toEan13(1));

		assertEquals(1, id);
	}

	@Test
	void toEan13() {
		long id = InventoryProduct.toEan13(1);

		assertEquals(2000000000015L, id);
	}

	@Test
	void fromEan13() {
		long id = InventoryProduct.fromEan13(2000000000015L);

		assertEquals(1, id);
	}

	@Test
	void getterMethods() {
		InventoryProduct product = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);

		final double PROFIT_MARGIN = 0.20;

		assertEquals("Kartoffeln", product.getName());
		assertEquals(BigDecimal.valueOf(4.99).multiply(BigDecimal.valueOf(PROFIT_MARGIN + 1)), product.getPrice());
		assertEquals(BigDecimal.valueOf(5.00).setScale(2, RoundingMode.DOWN), product.getUnit());
		assertEquals(0, product.getInventoryAmount());
		assertEquals(0, product.getDisplayedAmount());
		assertEquals(ProductCategory.FOOD_FRUIT_VEG, product.getProductCategory());
		assertEquals(MwStCategory.REDUCED, product.getMwStCategory());
		assertEquals(null, product.getPfandPrice());
		assertEquals(Organization.BIOLAND, product.getOrganization());
	}

}