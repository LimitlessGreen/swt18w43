package bioladen.order;

import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
class OrderItemTest {

	@Autowired
	private DistributorProductCatalog distributorProductCatalog;

	@Autowired
	private FilterChainProxy securityFilterChain;


	@Test
	void OrderItem() {
		OrderItem orderItem = new OrderItem(Quantity.of(1), distributorProductCatalog.findById(6L).get());
		Quantity quantity = Quantity.of(1);
		DistributorProduct distributorProduct = distributorProductCatalog.findById(6L).get();

		assertEquals("Milch", distributorProduct.getName());
		assertEquals(BigDecimal.valueOf(1.50).setScale(2, RoundingMode.HALF_EVEN), distributorProduct.getPrice());

	}
}