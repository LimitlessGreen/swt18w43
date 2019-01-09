package bioladen.product.label;

import bioladen.product.*;
import bioladen.product.distributor.Distributor;
import bioladen.product.distributor.DistributorRepository;
import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.salespointframework.quantity.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
public class PdfLabelGeneratorTest {
	@Autowired WebApplicationContext context;
	@Autowired FilterChainProxy securityFilterChain;
	@Autowired DistributorRepository distributorRepository;
	@Autowired DistributorProductCatalog distributorProductCatalog;
	@Autowired InventoryProductCatalog inventoryProductCatalog;


	protected MockMvc mvc;

	@BeforeAll
	public void setUp() {

		context.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);

		mvc = MockMvcBuilders.webAppContextSetup(context).//
				addFilters(securityFilterChain).//
				build();
	}

	@Test
	public void generateTest() throws Exception {
		distributorRepository.save(
				new Distributor(
						"Bauer Heinze",
						"heinze@bauern.de",
						"Heinz Heinze",
						"01524506154"
				)
		);

		distributorProductCatalog.save(
				new DistributorProduct(
						"Kakao",
						distributorRepository.findById(1L).get(),
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

		InventoryProduct inventoryProduct = new InventoryProduct(
				distributorProductCatalog.findById(1L).get(),
				distributorProductCatalog);

		inventoryProductCatalog.save(inventoryProduct);

		PdfLabelGenerator plg = new PdfLabelGenerator(inventoryProductCatalog);
		OutputStream os = new OutputStream() {
			@Override
			public void write(int i) throws IOException {

			}
		};

		plg.generate(1L, os);
	}
}
