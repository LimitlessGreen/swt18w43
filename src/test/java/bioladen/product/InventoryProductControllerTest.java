package bioladen.product;

import bioladen.product.distributor_product.DistributorProductCatalog;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class InventoryProductControllerTest {

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
	void productListAllowPublicAccess() throws Exception {
		mvc.perform(get("/productlist"))
				.andExpect(status().isOk());
	}

	@Test
	void productListIsAccessibleForManager() throws Exception {
		mvc.perform(get("/productlist").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}

	@Test
	void inventoryProductAdd() throws Exception {
		mvc.perform(get("/productlist/add").with(user("manager").roles("MANAGER"))
				.param("id", "1"))
				.andDo(print())
				.andExpect(redirectedUrl("/productlist"));
	}

	@Test
	void labelTest() throws Exception {
		mvc.perform(get("/product/label").with(user("manager").roles("MANAGER"))
				.param("id", "1"))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void labelsTest() throws Exception {
		mvc.perform(get("/productlist/labels").with(user("manager").roles("MANAGER")))
				.andDo(print())
				.andExpect(status().isOk());
	}
}