package bioladen.finances;

import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import bioladen.product.distributor_product.DistributorProductCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class CashierSystemTest {

	@Autowired WebApplicationContext context;
	@Autowired FilterChainProxy securityFilterChain;
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
	void cashiersystemPreventPublicAccess() throws Exception {

		mvc.perform(get("/cashiersystem"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void cashiersystemIsAccessibleForManager() throws Exception {
		mvc.perform(get("/cashiersystem").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("shoppingCart"));
	}

	@Test
	void cashiersystemAdd() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setDisplayedAmount(5);
		inventoryProductCatalog.save(inventoryProduct);

		mvc.perform(post("/cashiersystemAdd").with(user("manager").roles("MANAGER"))
				.param("pid", "1").param("amount", "1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemAddWithNoIdPresent() throws Exception {
		mvc.perform(post("/cashiersystemAdd").with(user("manager").roles("MANAGER"))
				.param("pid", "1").param("amount", "1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemAddWithDisplayedAmountTooLow() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setDisplayedAmount(0);
		inventoryProductCatalog.save(inventoryProduct);

		mvc.perform(post("/cashiersystemAdd").with(user("manager").roles("MANAGER"))
				.param("pid", "1").param("amount", "1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemAddRemoveItemIfAmountBelowZero() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setDisplayedAmount(1);
		inventoryProductCatalog.save(inventoryProduct);

		mvc.perform(post("/cashiersystemAdd").with(user("manager").roles("MANAGER"))
				.param("pid", "1").param("amount", "1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));

		mvc.perform(post("/cashiersystemAdd").with(user("manager").roles("MANAGER"))
				.param("pid", "1").param("amount", "-2"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemAddWithEan13Code() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setDisplayedAmount(5);
		inventoryProductCatalog.save(inventoryProduct);

		mvc.perform(post("/cashiersystemAdd").with(user("manager").roles("MANAGER"))
				.param("pid", "2000000000015").param("amount", "1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemDeleteCartItemWithoutId() throws Exception {
		mvc.perform(post("/deleteCartItem").with(user("manager").roles("MANAGER")))
				.andExpect(status().is4xxClientError());
	}

	/*@Test
	void cashiersystemDeleteCartItem() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setDisplayedAmount(5);
		inventoryProductCatalog.save(inventoryProduct);

		mvc.perform(post("/cashiersystemAdd").with(user("manager").roles("MANAGER"))
				.param("pid", "1").param("amount", "1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));

		mvc.perform(post("/deleteCartItem").with(user("manager").roles("MANAGER"))
				.param("cartItemId", ))
				.andExpect(status().isOk());
	}*/

	@Test
	void cashiersystemCalcChange() throws Exception {
		mvc.perform(post("/cashiersystemCalcChange").with(user("manager").roles("MANAGER"))
				.param("changeInput", "10.00"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemCalcChangeBelowZero() throws Exception {
		mvc.perform(post("/cashiersystemCalcChange").with(user("manager").roles("MANAGER"))
				.param("changeInput", "-10.00"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemCalcChangeEmpty() throws Exception {
		mvc.perform(post("/cashiersystemCalcChange").with(user("manager").roles("MANAGER"))
				.param("changeInput", ""))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemUser() throws Exception {
		mvc.perform(post("/cashiersystemUser").with(user("manager").roles("MANAGER"))
				.param("userId", "1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemUserNoCustomerFound() throws Exception {
		mvc.perform(post("/cashiersystemUser").with(user("manager").roles("MANAGER"))
				.param("userId", "0"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemPfand() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setPfandPrice(BigDecimal.valueOf(0.15));
		inventoryProductCatalog.save(inventoryProduct);

		mvc.perform((post("/cashiersystemPfand").with(user("manager").roles("MANAGER"))
				.param("pid", "1")).param("amount", "1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemPfandProductWithoutPfandPrice() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setPfandPrice(null);
		inventoryProductCatalog.save(inventoryProduct);

		mvc.perform((post("/cashiersystemPfand").with(user("manager").roles("MANAGER"))
				.param("pid", "1")).param("amount", "1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemAbort() throws Exception {
		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(1L).get(), distributorProductCatalog);
		inventoryProduct.setDisplayedAmount(5);
		inventoryProductCatalog.save(inventoryProduct);

		mvc.perform(post("/cashiersystemAdd").with(user("manager").roles("MANAGER"))
				.param("pid", "1").param("amount", "1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));

		mvc.perform(post("/cashiersystemAbort").with(user("manager").roles("MANAGER")))
				.andExpect(redirectedUrl("/cashiersystem"));
	}

	@Test
	void cashiersystemFinish() throws Exception {
		mvc.perform(post("/cashiersystemFinish").with(user("manager").roles("MANAGER")))
				.andExpect(redirectedUrl("/cashiersystem"));
	}


}