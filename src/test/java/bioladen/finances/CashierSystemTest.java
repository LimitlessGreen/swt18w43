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
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertTrue;
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


	protected MockMvc mvc;
	private ShoppingCart shoppingCart = new ShoppingCart();
	Model model = new ExtendedModelMap();

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
		mvc.perform(post("/cashiersystemAdd").with(user("manager").roles("MANAGER"))
				.param("pid", "1").param("amount", "1"))
				.andDo(print())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));

	}

	@Test
	void cashiersystemDeleteCartItemWithoutId() throws Exception {
		mvc.perform(post("/deleteCartItem").with(user("manager").roles("MANAGER")))
				.andExpect(status().is4xxClientError());
	}


	@Test
	void cashiersystemCalcChange() throws Exception {
		mvc.perform(post("/cashiersystemCalcChange").with(user("manager").roles("MANAGER"))
				.param("changeInput", "10.00"))
				.andDo(print())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemUser() throws Exception {
		mvc.perform(post("/cashiersystemUser").with(user("manager").roles("MANAGER"))
				.param("userId", "1"))
				.andDo(print())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemPfand() throws Exception {
		mvc.perform((post("/cashiersystemPfand").with(user("manager").roles("MANAGER"))
				.param("pid", "1")).param("amount", "1"))
				.andDo(print())
				.andExpect(model().attributeHasNoErrors("shoppingCart"));
	}

	@Test
	void cashiersystemAbort() throws Exception {
		mvc.perform(post("/cashiersystemAbort").with(user("manager").roles("MANAGER")))
				.andExpect(redirectedUrl("/cashiersystem"));
	}

	@Test
	void cashiersystemFinish() throws Exception {
		mvc.perform(post("/cashiersystemFinish").with(user("manager").roles("MANAGER")))
				.andExpect(redirectedUrl("/cashiersystem"));
	}


}