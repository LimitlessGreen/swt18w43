package bioladen.finances;

import bioladen.customer.Customer;
import bioladen.customer.CustomerType;
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
class ShoppingCartTest {

	private ShoppingCart shoppingCart = new ShoppingCart();

	@MockBean
	private DistributorProductCatalog distributorProductCatalog;

	@MockBean
	private InventoryProductCatalog inventoryProductCatalog;

	@MockBean
	private DistributorRepository distributorRepository;
	

	private Customer customer = new Customer("Hans",
									"Hanserich",
									"Hans@gmx.de",
									Customer.Sex.MALE,
									CustomerType.HOUSE_CUSTOMER);
	
	private final int one = 1;
	private final int two = 2;
	private final int five = 5;
	private final int seven = 7;

	/*
	 * FIXME: Adapt Tests to new DistributorProduct (with category and pfand)
	 */

//	@Test
//	void addOrUpdateItem() {
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
//		shoppingCart.addOrUpdateItem(inventoryProduct, five);
//		assertTrue(shoppingCart.getItems().containsKey(inventoryProduct));
//	}

//	@Test
//	void removeItem() {
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
//		CartCartItem item = shoppingCart.addOrUpdateItem(inventoryProduct, five);
//
//		shoppingCart.removeItem(item.getId());
//		assertFalse(shoppingCart.getItems().containsKey(inventoryProduct));
//	}

//	@Test
//	void getItem() {
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
//		CartCartItem item = shoppingCart.addOrUpdateItem(inventoryProduct, five);
//
//		assertEquals(item, shoppingCart.getItem(item.getId()).get());
//	}

	@Test
	void clear() {
		shoppingCart.clear();
		assertTrue(shoppingCart.isEmpty());
		assertNull(shoppingCart.getCustomer());
	}

	@Test
	void isEmpty() {
		shoppingCart.clear();

		assertTrue(shoppingCart.getItems().isEmpty());
	}

//	@Test
//	void getPrice() {
//		shoppingCart.setCustomer(customer);
//
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

//		CartCartItem item = shoppingCart.addOrUpdateItem(inventoryProduct, one);
//		BigDecimal price = shoppingCart.getItem(item.getId()).get().getPrice(); // quantity = one --> price = price of product
//		double discount = Customer.getDiscount(customer.getCustomerType());

//		assertEquals(price.multiply(BigDecimal.valueOf(one - discount)).setScale(two), shoppingCart.getPrice());
//	}

//	@Test
//	void getBasicPrice() {
//		shoppingCart.setCustomer(customer);
//
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
//		CartCartItem item = shoppingCart.addOrUpdateItem(inventoryProduct, one);
//		BigDecimal price = shoppingCart.getItem(item.getId()).get().getPrice(); // quantity = one --> price = price of product
//		double discount = Customer.getDiscount(customer.getCustomerType());
//
//		assertEquals(price.setScale(two), shoppingCart.getBasicPrice());
//	}

//	@Test
//	void getAmountOfItems() {
//		shoppingCart.setCustomer(customer);
//
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
//		shoppingCart.addOrUpdateItem(inventoryProduct, two);
//
//		assertEquals(one, shoppingCart.getAmountOfItems());
//	}

	@Test
	void getCustomerDiscountString() {
		shoppingCart.clear();
		assertEquals("0.0%", shoppingCart.getCustomerDiscountString());

		shoppingCart.setCustomer(customer);
		assertEquals("5.0%", shoppingCart.getCustomerDiscountString());
	}

	@Test
	void getDiscount() {
		final double DISCOUNT = 0.05;

		shoppingCart.clear();
		assertEquals(0, shoppingCart.getDiscount());

		shoppingCart.setCustomer(customer);
		assertEquals(DISCOUNT, shoppingCart.getDiscount());
	}

}