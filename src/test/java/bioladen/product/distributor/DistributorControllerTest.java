package bioladen.product.distributor;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class DistributorControllerTest {

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
	void distributorListPreventPublicAccess() throws Exception {
		mvc.perform(get("/distributorlist"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void distributorListIsAccessibleForManager() throws Exception {
		mvc.perform(get("/distributorlist").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}

	@Test
	void distributorFormIsAccessibleForManager() throws Exception {
		mvc.perform(get("/distributorform").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}

	@Test
	void distributorAdd() throws Exception {
		mvc.perform(post("/distributorform").with(user("manager").roles("MANAGER"))
				.param("name", "Bauer Heinze").param("email", "heinze@bauern.de")
				.param("contactName", "Heinz Heinze").param("phone", "03855651223"))
				.andDo(print())
				.andExpect(redirectedUrl("/distributorlist"));
	}

	@Test
	void distributorDelete() throws Exception {
		mvc.perform(get("/distributorlist/delete").with(user("manager").roles("MANAGER"))
				.param("id", "1"))
				.andDo(print())
				.andExpect(redirectedUrl("/distributorlist"));
	}

}