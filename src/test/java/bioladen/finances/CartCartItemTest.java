package bioladen.finances;

import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import bioladen.product.distributor.Distributor;
import bioladen.product.distributor.DistributorRepository;
import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class CartCartItemTest {

	@MockBean
	private DistributorProductCatalog distributorProductCatalog;

	@MockBean
	private InventoryProductCatalog inventoryProductCatalog;

	@MockBean
	private DistributorRepository distributorRepository;


	private final int one = 1;
	private final int five = 5;
	private final int seven = 7;

	/*
	 * FIXME: Adapt Tests to new DistributorProduct (with category and pfand)
	 */

	//@Test
	//void getProductName() {
	//	Distributor distributor = new Distributor("Heinz",
	//			"heinz@bauern.de",
	//			"Hermann",
	//			"012354123");
	//	distributorRepository.save(distributor);
	//	DistributorProduct distributorProduct = new DistributorProduct("Milch",
	//			distributor,
	//			BigDecimal.valueOf(five),
	//			BigDecimal.valueOf(one),
	//			seven);
	//	distributorProductCatalog.save(distributorProduct);
	//	InventoryProduct inventoryProduct = new InventoryProduct(distributorProduct, distributorProductCatalog);
	//	inventoryProductCatalog.save(inventoryProduct);
//
//		CartCartItem cartCartItem = new CartCartItem(inventoryProduct, 2);
//
//		assertEquals(inventoryProduct.getName(), cartCartItem.getProductName());
//	}

//	@Test
//	void add() {
//		Distributor distributor = new Distributor("Heinz",
//				"heinz@bauern.de",
//				"Hermann",
//				"012354123");
//		distributorRepository.save(distributor);
//		DistributorProduct distributorProduct = new DistributorProduct("Milch",
//				distributor,
//				BigDecimal.valueOf(five),
//				BigDecimal.valueOf(one),
//				seven);
//		distributorProductCatalog.save(distributorProduct);
//		InventoryProduct inventoryProduct = new InventoryProduct(distributorProduct, distributorProductCatalog);
//		inventoryProductCatalog.save(inventoryProduct);
//
//		CartCartItem cartCartItem = new CartCartItem(inventoryProduct, 2);
//
//		assertEquals(cartCartItem.getQuantity() + 2, cartCartItem.add(2).getQuantity());
//	}
}