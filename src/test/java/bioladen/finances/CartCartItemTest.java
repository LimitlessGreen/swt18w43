package bioladen.finances;

import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import bioladen.product.distributor.Distributor;
import bioladen.product.distributor.DistributorRepository;
import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class CartCartItemTest {

	@Autowired
	private DistributorProductCatalog distributorProductCatalog;

	@Autowired
	private InventoryProductCatalog inventoryProductCatalog;



	@Test
	void getProductName() {

		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);

		CartCartItem cartCartItem = new CartCartItem(inventoryProduct, 2);

		assertEquals(inventoryProduct.getName(), cartCartItem.getProductName());
	}

	@Test
	void add() {

		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);

		CartCartItem cartCartItem = new CartCartItem(inventoryProduct, 2);

		assertEquals(cartCartItem.getQuantity() + 2, cartCartItem.add(2).getQuantity());
	}
}