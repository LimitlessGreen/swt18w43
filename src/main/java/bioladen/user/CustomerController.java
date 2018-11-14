package bioladen.user;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CustomerController {
	private final CustomerManagement customerManagement;

	CustomerController(CustomerManagement customerManagement) {

		Assert.notNull(customerManagement, "CustomerManagement must not be null!");

		this.customerManagement = customerManagement;
	}

	// (｡◕‿◕｡)
	// Über @Valid können wir die Eingaben automagisch prüfen lassen, ob es Fehler gab steht im BindingResult,
	// dies muss direkt nach dem @Valid Parameter folgen.
	// Siehe außerdem videoshop.model.validation.RegistrationForm
	// Lektüre: http://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html
	@PostMapping("/register")
	String registerNew(@Valid RegistrationForm form, Errors result) {

		if (result.hasErrors()) {
			return "register";
		}

		// (｡◕‿◕｡)
		// Falles alles in Ordnung ist legen wir einen Customer an
		//ustomerManagement.createCustomer(form);

		return "redirect:/";
	}

	@GetMapping("/register")
	String register(Model model, RegistrationForm form) {
		model.addAttribute("form", form);
		return "register";
	}

	@GetMapping("/customerlist")
	//@PreAuthorize("hasRole('ROLE_BOSS')")
	String customers(Model model) {

		model.addAttribute("customerList", customerManagement.findAll());

		return "customers";
	}
}
