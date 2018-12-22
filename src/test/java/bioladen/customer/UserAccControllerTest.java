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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class UserAccControllerTest {
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
	void profilPreventPublicAccess() throws Exception {
		mvc.perform(get("/profil"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void profilIsAccessibleForManager() throws Exception {
		mvc.perform(get("/profil").with(user("manager").roles("MANAGER")))
				.andExpect(status().isOk());
	}

	@Test
	void profilIsAccessibleForStaff() throws Exception {
		mvc.perform(get("/profil").with(user("staff").roles("STAFF")))
				.andExpect(status().isOk());
	}

	@Test
	void resetPreventPublicAccess() throws Exception {
		mvc.perform(get("/customerlist/resetPassword").param("email", "bertabunt@bio.de"))
				.andExpect(status().isFound())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void resetIsAccessibleForManager() throws Exception {
		mvc.perform(get("/customerlist/resetPassword").with(user("feldfreude@bio.de").roles("MANAGER")).param("email", "bertabunt@bio.de"))
				.andExpect(redirectedUrl("/customerlist"));
	}

	@Test
	void postiveResetPassword() throws Exception {
		mvc.perform((((post("/profil").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("oldPassword", "blattgrün43")).param("newPassword", "Feldfreude43")
				.param("newPasswordAgain", "Feldfreude43"))))
				.andDo(print());
		mvc.perform(get("/customerlist/resetPassword").with(user("feldfreude@bio.de").roles("MANAGER")).param("email", "feldfreude@bio.de"))
				.andExpect(redirectedUrl("/customerlist"));
	}

	@Test
	void userAccModify() throws Exception {
		mvc.perform((((post("/profil").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("oldPassword", "blattgrün43")).param("newPassword", "Feldfreude43")
				.param("newPasswordAgain", "Feldfreude43"))))
				.andDo(print());

	}

	@Test
	void errorUserAccModify() throws Exception {
		mvc.perform((((post("/profil").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("oldPassword", "IDontKnow")).param("newPassword", "Feldfreude43")
				.param("newPasswordAgain", "Feldfreude43"))))
				.andExpect(model().attribute("errorPasswordMsg", "Altes Passwort ist inkorrekt."))
				.andDo(print());
		mvc.perform((((post("/profil").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("oldPassword", "blattgrün43")).param("newPassword", "feldfreude43")
				.param("newPasswordAgain", "Feldfreude43"))))
				.andExpect(model().attribute("errorPasswordMsg", "Die neuen Passwörter stimmen nicht überein."))
				.andDo(print());
		mvc.perform((((post("/profil").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("oldPassword", "blattgrün43")).param("newPassword", "blattgrün43")
				.param("newPasswordAgain", "blattgrün43"))))
				.andExpect(model().attribute("errorPasswordMsg", "Neues Passwort stimmt mit dem alten überein."))
				.andDo(print());
		mvc.perform((((post("/profil").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("oldPassword", "   ")).param("newPassword", "            ")
				.param("newPasswordAgain", ""))))
				.andExpect(model().attribute("errorPasswordMsg", "Einige Felder wurden nicht ausgefüllt."))
				.andDo(print());
		mvc.perform((((post("/profil").with(user("feldfreude@bio.de").roles("MANAGER"))
				.param("oldPassword", "blattgrün43")).param("newPassword", "            ")
				.param("newPasswordAgain", ""))))
				.andExpect(model().attribute("errorPasswordMsg", "Einige Felder wurden nicht ausgefüllt."))
				.andDo(print());
	}




}