package bioladen.customer;

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
class CustomerControllerTest {

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
	void customerlistPreventPublicAccess() throws Exception {

		mvc.perform(get("/customerlist"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void customerlistIsAccessibleForManager() throws Exception {
		mvc.perform(get("/customerlist").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}

	@Test
	void customerRegister() throws Exception {
		mvc.perform((((post("/register").with(user("manager").roles("MANAGER"))
				.param("firstname", "Flori")).param("lastname", "Feldfreude")
				.param("phone", "123123739").param("email", "flori@feldfreude.de"))
				.param("sex", "male")).param("address", "Feldweg 43, 24242 Felde").param("type", "Manager"))
				.andDo(print())
				.andExpect(status().isOk());
	}

}