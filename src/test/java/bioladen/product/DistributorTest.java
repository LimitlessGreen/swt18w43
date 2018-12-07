package bioladen.product;

import bioladen.product.distributor.Distributor;
import bioladen.product.distributor.DistributorRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class DistributorTest {

	@Autowired
	private DistributorRepository distributorRepository;


	@Test
	void getterMethods() {
		Distributor distributor = distributorRepository.findById(1L).get();

		assertEquals(1, (long)distributor.getId());
		assertEquals("Bauer Heinze", distributor.getName());
		assertEquals("heinze@bauern.de", distributor.getEmail());
		assertEquals("Heinz Heinze", distributor.getContactName());
		assertEquals("01524506154", distributor.getPhone());

	}

}