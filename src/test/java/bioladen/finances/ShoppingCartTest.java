package bioladen.finances;

import bioladen.customer.Customer;
import bioladen.customer.CustomerType;
import bioladen.customer.Sex;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class ShoppingCartTest {

	private ShoppingCart shoppingCart = new ShoppingCart();

	@Autowired
	private DistributorProductCatalog distributorProductCatalog;

	@Autowired
	private InventoryProductCatalog inventoryProductCatalog;


	private Customer customer = new Customer("Hans",
									"Hanserich",
									"Hans@gmx.de",
									Sex.MALE,
									CustomerType.HOUSE_CUSTOMER);
	
	private final int one = 1;
	private final int two = 2;
	private final int five = 5;



	@Test
	void addOrUpdateItem() {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);

		shoppingCart.addOrUpdateItem(inventoryProduct, five);
		assertTrue(shoppingCart.getItems().containsKey(inventoryProduct));
	}

	@Test
	void addOrUpdateItemAlreadyInItems() {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProductCatalog.save(inventoryProduct);

		shoppingCart.addOrUpdateItem(inventoryProduct, five);
		shoppingCart.addOrUpdateItem(inventoryProduct, two);
		assertTrue(shoppingCart.getItems().containsKey(inventoryProduct));
	}

	@Test
	void removeItem() {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);

		CartCartItem item = shoppingCart.addOrUpdateItem(inventoryProduct, five);

		shoppingCart.removeItem(item.getId());
		assertFalse(shoppingCart.getItems().containsKey(inventoryProduct));
	}

	@Test
	void getItem() {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);

		CartCartItem item = shoppingCart.addOrUpdateItem(inventoryProduct, five);

		assertEquals(item, shoppingCart.getItem(item.getId()).get());
	}

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

	@Test
	void getPrice() {
		shoppingCart.setCustomer(customer);

		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(6L).get(), distributorProductCatalog);
		inventoryProduct.setPfandPrice(BigDecimal.valueOf(0.15));

		CartCartItem item = shoppingCart.addOrUpdateItem(inventoryProduct, one);
		BigDecimal price = shoppingCart.getItem(item.getId()).get().getPrice(); // quantity = one --> price = price of product
		double discount = customer.getCustomerType().getDiscount();
		price = price.add(item.getInventoryProduct().getPfandPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
		price = price.multiply(BigDecimal.valueOf(one - discount));
		price = price.add(shoppingCart.getMwstMoney());

		assertEquals(price.setScale(two, RoundingMode.HALF_EVEN), shoppingCart.getPrice());
	}

	@Test
	void getBasicPrice() {
		shoppingCart.setCustomer(customer);

		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(6L).get(), distributorProductCatalog);
		inventoryProduct.setPfandPrice(BigDecimal.valueOf(0.15));

		CartCartItem item = shoppingCart.addOrUpdateItem(inventoryProduct, one);
		BigDecimal price = shoppingCart.getItem(item.getId()).get().getPrice(); // quantity = one --> price = price of product
		price = price.add(item.getInventoryProduct().getPfandPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
		double discount = customer.getCustomerType().getDiscount();

		assertEquals(price.setScale(two, RoundingMode.HALF_EVEN), shoppingCart.getBasicPrice());
	}

	@Test
	void getAmountOfItems() {
		shoppingCart.setCustomer(customer);

		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);

		shoppingCart.addOrUpdateItem(inventoryProduct, two);

		assertEquals(one, shoppingCart.getAmountOfItems());
	}

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

	@Test
	void addOrUpdatePfand() {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setPfandPrice(BigDecimal.valueOf(0.15));
		inventoryProductCatalog.save(inventoryProduct);

		shoppingCart.addOrUpdatePfand(inventoryProduct.getPfandPrice(), 1L);
		shoppingCart.addOrUpdatePfand(inventoryProduct.getPfandPrice(), 1L);
		assertTrue(shoppingCart.getPfand().containsKey(inventoryProduct.getPfandPrice()));
	}

	@Test
	void getPfandMoney() {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setPfandPrice(BigDecimal.valueOf(0.15));
		inventoryProductCatalog.save(inventoryProduct);

		shoppingCart.addOrUpdatePfand(inventoryProduct.getPfandPrice(), 1L);
		assertEquals(BigDecimal.valueOf(-0.15), shoppingCart.getPfandMoney());
	}

	@Test
	void getSaleMoney() {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setPrice(BigDecimal.valueOf(1));
		InventoryProduct inventoryProduct2 = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct2.setPrice(BigDecimal.valueOf(1));
		inventoryProductCatalog.save(inventoryProduct);
		inventoryProductCatalog.save(inventoryProduct2);

		shoppingCart.addOrUpdateItem(inventoryProduct, 1);
		shoppingCart.addOrUpdateItem(inventoryProduct2, 1);
		assertEquals(BigDecimal.valueOf(2).setScale(2, RoundingMode.HALF_EVEN), shoppingCart.getSaleMoney());
	}

}