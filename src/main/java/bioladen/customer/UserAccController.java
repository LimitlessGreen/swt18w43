package bioladen.customer;

import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Transactional
public class UserAccController {
	private final UserAccountManager userAccountManager;

	UserAccController(UserAccountManager userAccountManager){
		this.userAccountManager = userAccountManager;
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@RequestMapping("/profil")
	public String account() {

		return "profil";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@PostMapping("/profil")
	public String changePassword() {
		return "profil";
	}
}
