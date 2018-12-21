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
				.param("firstname", "Peter")).param("lastname", "Wurst")
				.param("phone", "23623626").param("email", "wurstpeter@gmx.de"))
				.param("sex", "male")).param("address", "Fleischerrei 67, 16928 Kuhbier").param("type", "House"))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void registerIsAccessibleForPersonal() throws Exception {
		mvc.perform(get("/register").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}

	@Test
	void registerPreventPublicAccess() throws Exception {
		mvc.perform(get("/register"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void deletePreventPublicAccess() throws Exception {
		mvc.perform(get("/customerlist/delete").param("id", "2"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void deleteIsAccessibleForManager() throws Exception {
		mvc.perform(get("/customerlist/delete").with(user("feldfreude@bio.de").roles("MANAGER")).param("id", "2"))
				.andExpect(redirectedUrl("/customerlist"));
	}

	@Test
	void customerModifyStaffToManager() throws Exception {
		mvc.perform((((post("/modified").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("firstname", "Berta")).param("lastname", "Balsamico")
				.param("phone", "235235").param("email", "bertabunt@bio.de"))
				.param("sex", "female")).param("address", "Essig 1 032423 Mozzarella").param("type", "Manager").param("id", "2"))
				.andDo(print())
				.andExpect(redirectedUrl("/customerlist"));

	}

	@Test
	void customerModifyStaffToNormalCustomer() throws Exception {
		mvc.perform((((post("/modified").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("firstname", "Berta")).param("lastname", "Balsamico")
				.param("phone", "235235").param("email", "bertabunt@bio.de"))
				.param("sex", "female")).param("address", "Essig 1 032423 Mozzarella").param("type", "House").param("id", "2"))
				.andDo(print())
				.andExpect(redirectedUrl("/customerlist"));

	}

	@Test
	void customerModifyCustomerToStaff() throws Exception {
		mvc.perform((((post("/modified").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("firstname", "Hilde")).param("lastname", "Garten")
				.param("phone", "  ").param("email", "garten@obst.de"))
				.param("sex", "female")).param("address", "Hof 4, 06862 Hundeluft").param("type", "Staff").param("id", "3"))
				.andDo(print())
				.andExpect(redirectedUrl("/customerlist"));

	}

	@Test
	void customerModifyOtherCases() throws Exception {
		mvc.perform((((post("/modified").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("firstname", "Hildegard")).param("lastname", "Garten-Obst")
				.param("phone", "").param("email", "garten@obst.de"))
				.param("sex", "female")).param("address", "").param("type", "Major").param("id", "3"))
				.andDo(print())
				.andExpect(redirectedUrl("/customerlist"));

	}

	@Test
	void modifyPreventPublicAccess() throws Exception {
		mvc.perform(get("/customerlist/modify").param("id", "2"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}



}