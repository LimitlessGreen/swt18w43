package bioladen.product.distributor_product;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class DistributorProductControllerTest {

	@Autowired WebApplicationContext context;
	@Autowired FilterChainProxy securityFilterChain;

	protected MockMvc mvc;

	@BeforeAll
	public void setUp() {

		context.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);

		mvc = MockMvcBuilders.webAppContextSetup(context).//
				addFilters(securityFilterChain).//
				build();
	}


	@Test
	void distributorProductListPreventPublicAccess() throws Exception {
		mvc.perform(get("/distributorproductlist"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void distributorProductListIsAccessibleForManager() throws Exception {
		mvc.perform(get("/distributorproductlist").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}

	@Test
	void distributorProductFormisAccessibleForManager() throws Exception {
		mvc.perform(get("/addDistributorProduct").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}

	@Test
	void distributorProductAdd() throws Exception {
		mvc.perform(post("/addDistributorProduct").with(user("manager").roles("MANAGER"))
				.param("name", "Milch").param("distributor", "1").param("price", "2.00")
				.param("unit", "1").param("unitMetric", "LITER").param("minimumOrderAmount", "12")
				.param("productCategory", "FOOD_DAIRY").param("mwStCategory", "REDUCED")
				.param("pfandPrice", "0.15").param("organization", "BIOLAND"))
				.andDo(print())
				.andExpect(redirectedUrl("/distributorproductlist"));
	}

}