package bioladen.customer;

import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.salespointframework.useraccount.AuthenticationManager;

import java.util.List;

@Controller
@Transactional
public class CustomerController implements ApplicationEventPublisherAware {

	private final CustomerRepository customerRepository;
	private final AuthenticationManager authenticationManager;

	CustomerController(CustomerRepository customerRepository, AuthenticationManager authenticationManager) {
		this.customerRepository = customerRepository;
		this.authenticationManager = authenticationManager;
	}

	/*Functions for register.html*/
	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@RequestMapping("/register")
	public String register() {
		return "register";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@PostMapping("/register")
	public String registerNew(@RequestParam("firstname") String firstname,
							  @RequestParam("lastname")  String lastname,
							  @RequestParam("phone")     String phone,
							  @RequestParam("email")     String email,
							  @RequestParam("sex")       String sex,
							  @RequestParam("address")   String address,
							  @RequestParam("type")      String type,
							  Model model){

		CustomerType customerType;
		Sex customerSex;

		switch (sex) {
			case "male":
				customerSex = Sex.MALE;
				break;
			case "female":
				customerSex = Sex.FEMALE;
				break;
			case "various":
				customerSex = Sex.VARIOUS;
				break;
			default:
				throw new IllegalArgumentException(sex);
		}

		switch (type) {
			case "Manager":
				customerType = CustomerType.MANAGER;
				break;
			case "Staff":
				customerType = CustomerType.STAFF;
				break;
			case "Major":
				customerType = CustomerType.MAJOR_CUSTOMER;
				break;
			case "House":
				customerType = CustomerType.HOUSE_CUSTOMER;
				break;
			default:
				throw new IllegalArgumentException(type);
		}

		String safeFirstName;
		String safeLastName;
		String safeEmail = "";

		if (firstname.isEmpty()) {
			model.addAttribute("errorRegister", true);
			model.addAttribute("errorRegisterMsg", "Pflichtfelder wurde nicht aufgefüllt.");
			return "register";
		} else {
			safeFirstName = firstname;
		}

		if (lastname.isEmpty()) {
			model.addAttribute("errorRegister", true);
			model.addAttribute("errorRegisterMsg", "Pflichtfelder wurde nicht aufgefüllt.");
			return "register";
		} else {
			safeLastName = lastname;
		}

		if (email.isEmpty()) {
			model.addAttribute("errorRegister", true);
			model.addAttribute("errorRegisterMsg", "Pflichtfelder wurde nicht aufgefüllt.");
			return "register";
		} else {
			if (!customerRepository.findAll().isEmpty()) {
				for (Customer customer : customerRepository.findAll()) {
					if (!customer.getEmail().equals(email)) {
						safeEmail = email;
					} else {
						model.addAttribute("errorRegister", true);
						model.addAttribute("errorRegisterMsg", "E-Mail ist bereits im System registriert");
						return "register";
					}
				}
			} else {
				safeEmail = email;
			}
		}


		Customer customer = new Customer(safeFirstName, safeLastName, safeEmail, customerSex, customerType);

		if (!phone.isEmpty()) {
			customer.setPhone(phone);
		}
		if (!address.isEmpty()) {
			customer.setStreet(address);
		}
		customerRepository.save(customer);

		// (👁 ᴥ 👁) Event
		publishEvent(customer, EntityLevel.CREATED);

		model.addAttribute("successRegister", true);
		return "register";
	}

	/*Function for customerlist.html*/

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@GetMapping("/customerlist")
	String customerRepository(Model model) {
		List<Customer> customerList = customerRepository.findAll();
		model.addAttribute("customerList", customerList);

		return "customerlist";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/customerlist/delete")
	String deleteCustomer(@RequestParam Long id) {
		Customer customer = customerRepository.findById(id).get();
		if (!authenticationManager.getCurrentUser().get().getUsername().
				equals(customerRepository.findById(id).get().getEmail())) {
			customerRepository.deleteById(id);

			// (👁 ᴥ 👁) Event
			publishEvent(customer, EntityLevel.DELETED);
		}


		return "redirect:/customerlist";
	}

	/*
	 _________________
	< Event publisher >
	 -----------------
        \   ^__^
         \  (@@)\_______
            (__)\       )\/\
                ||----w |
                ||     ||

	*/
	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	private void publishEvent(Customer customer, EntityLevel entityLevel) {
		publisher.publishEvent(new EntityEvent<>(customer, entityLevel));
	}
}

