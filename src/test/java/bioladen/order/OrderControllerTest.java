package bioladen.order;

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

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class OrderControllerTest {

	@Autowired
	WebApplicationContext context;
	@Autowired
	FilterChainProxy securityFilterChain;


	protected MockMvc mvc;

	@BeforeAll
	public void setUp() {

		context.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);

		mvc = MockMvcBuilders.webAppContextSetup(context).//
				addFilters(securityFilterChain).//
				build();
	}

	@Test
	void orderPagePublicAccess() throws Exception {
		mvc.perform(get("/order"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void orderPageIsAccessibleForManager() throws Exception {
		mvc.perform(get("/order").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}

	@Test
	void addItem() throws Exception {
		mvc.perform(get("/order/add").with(user("manager").roles("MANAGER"))
				.param("id", "1")
				.param("amount", "1"))
				.andDo(print())
				.andExpect(redirectedUrl("/order"))
				.andExpect(status().isFound());
	}

	@Test
	void deleteOrderPublicAccess() throws Exception {
		mvc.perform(get("/orders/delete").param("id", "1"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void deleteOrderIsAccessibleForManager() throws Exception {
		mvc.perform(get("/orders/delete").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("id", "1"))
				.andExpect(redirectedUrl("/orders"));


	}

	@Test
	void orderRemovePublicAccess() throws Exception {
		mvc.perform(get("/order/remove").param("id", "1"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void orderRemoveIsAccessibleForManager() throws Exception {
		mvc.perform(get("/order/remove").with(user("manager").roles("MANAGER")).param("id", "1"))
				.andExpect(status().isFound());
	}

	@Test
	void orderCompletePagePublicAccess() throws Exception {
		mvc.perform(get("/orders"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void orderCompletePageIsAccessibleForManager() throws Exception {
		mvc.perform(get("/orders").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}



}