package bioladen.newsletter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class NewsletterController {

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@RequestMapping("/admin")
	public String admin() {

		return "admin";
	}

	@PostMapping("/newsletter")
	public String newsletter(@RequestParam String message) {
		//TODO write Newsletter
		return "admin";
	}

	@RequestMapping("/cancel_newsletter")
	public String cancelNewsletter() {
		return "cancel_newsletter";
	}

	@PostMapping("/cancel_newsletter")
	public String cancelledNewsletter(@RequestParam String email,
									  Model model) {
		model.addAttribute("errorCancel",true);
		//TODO cancel Newsletter

		return "cancel_newsletter";
	}

	@PostMapping("register_newsletter")
	public String registerNewsletter(@RequestParam String email,
									 Model model) {
		model.addAttribute("errorRegister",true);
		//TODO register Newsletter
		return "welcome";
	}
}
