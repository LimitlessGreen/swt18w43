package bioladen.product;

import bioladen.product.distributor_product.DistributorProductCatalog;
import bioladen.product.stock_taking.StockTaking;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
public class StockTakingControllerTest {
	@Autowired WebApplicationContext context;
	@Autowired FilterChainProxy securityFilterChain;
	@Autowired DistributorProductCatalog distributorProductCatalog;
	@Autowired InventoryProductCatalog inventoryProductCatalog;
	@Autowired StockTaking stockTaking;

	protected MockMvc mvc;

	@BeforeAll
	public void setUp() {
		context.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);

		mvc = MockMvcBuilders.webAppContextSetup(context).//
				addFilters(securityFilterChain).//
				build();
	}

	@Test
	public void stockTakingAccessibleForManager() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setDisplayedAmount(5);
		inventoryProductCatalog.save(inventoryProduct);
		InventoryProduct inventoryProduct1 = new InventoryProduct(distributorProductCatalog.findById(2L).get(), distributorProductCatalog);
		inventoryProduct1.setDisplayedAmount(27);
		inventoryProductCatalog.save(inventoryProduct1);

		if (!stockTaking.isOnGoing()) {
			stockTaking.beginStockTaking();
		}

		stockTaking.registerInventoryAmount(inventoryProductCatalog.findById(1L).get(), 1);
		stockTaking.registerDisplayedAmount(inventoryProductCatalog.findById(1L).get(), 2);
		stockTaking.registerDisplayedAmount(inventoryProductCatalog.findById(2L).get(), 3);

		mvc.perform(get("/stockTaking").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}

	@Test
	public void stockTakingBeginAccessibleForManager() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setDisplayedAmount(5);
		inventoryProductCatalog.save(inventoryProduct);

		if (stockTaking.isOnGoing()) {
			stockTaking.finishStockTaking();
		}

		mvc.perform(get("/stockTaking/begin").with(user("manager").roles("MANAGER")))
				.andExpect(redirectedUrl("/productlist"));
	}

	@Test
	public void stockTakingFinishAccessibleForManager() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setDisplayedAmount(5);
		inventoryProductCatalog.save(inventoryProduct);

		mvc.perform(get("/stockTaking/finish").with(user("manager").roles("MANAGER")))
				.andExpect(redirectedUrl("/productlist"));
	}

	@Test
	public void stockTakingInventoryAmountEnterableAccessibleForManager() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setDisplayedAmount(5);
		inventoryProductCatalog.save(inventoryProduct);

		if (!stockTaking.isOnGoing()) {
			stockTaking.beginStockTaking();
		}

		mvc.perform(post("/product/inventoryStockTaking").with(user("manager").roles("MANAGER"))
				.param("id", "1")
				.param("countedAmount", "1"))
				.andExpect(redirectedUrl("/productlist"));
	}

	@Test
	public void stockTakingDisplayedAmountEnterableAccessibleForManager() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setDisplayedAmount(5);
		inventoryProductCatalog.save(inventoryProduct);

		if (!stockTaking.isOnGoing()) {
			stockTaking.beginStockTaking();
		}

		mvc.perform(post("/product/displayedStockTaking").with(user("manager").roles("MANAGER"))
				.param("id", "1")
				.param("countedAmount", "1"))
				.andExpect(redirectedUrl("/productlist"));
	}
}
