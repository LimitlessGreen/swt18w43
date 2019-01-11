package bioladen.newsletter;

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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class NewsletterControllerTest {

	@Autowired WebApplicationContext context;
	@Autowired FilterChainProxy securityFilterChain;
	@Autowired NewsletterManager newsletterManager;

	protected MockMvc mvc;

	@BeforeAll
	public void setUp() {

		context.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);

		mvc = MockMvcBuilders.webAppContextSetup(context).//
				addFilters(securityFilterChain).//
				build();
	}

	@Test
	void newsletterPreventPublicAccess() throws Exception {

		mvc.perform(get("/admin"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void newsletterIsAccessibleForManager() throws Exception {
		mvc.perform(get("/admin").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}

	@Test
	void newsletterCancelisAccessibleForEveryone() throws Exception {
		mvc.perform(get("/cancel_newsletter"))
				.andExpect(status().isOk());
	}

	@Test
	void newsletterCancelIfSubscribed() throws Exception {
		newsletterManager.subscribe("feldfreude@bio.de");

		mvc.perform(post("/cancel_newsletter")
				.param("email", "feldfreude@bio.de"))
				.andExpect(status().isOk());
	}

	@Test
	void newsletterCancelIfWrongInput() throws Exception {
		mvc.perform(post("/cancel_newsletter")
				.param("email", "1234"))
				.andExpect(model().attribute("errorCancel", true))
				.andExpect(status().isOk());
	}

	@Test
	void newsletterRegisterIfNotSubscribed() throws Exception {
		mvc.perform(post("/register_newsletter")
				.param("email", "feldfreude@bio.de"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("successRegister", true));
	}

	@Test
	void newsletterRegisterIfAlreadySubscribed() throws Exception {
		newsletterManager.subscribe("feldfreude@bio.de");

		mvc.perform(post("/register_newsletter")
				.param("email", "feldfreude@bio.de"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("errorRegister", true));
	}

	@Test
	void newsletterSendingEmail() throws Exception {
		mvc.perform(post("/sendEmail").with(user("manager").roles("MANAGER"))
				.param("text", "Hallo Welt")
				.param("subject", "Newsletter Test"))
				.andExpect(redirectedUrl("/admin"));
	}

}