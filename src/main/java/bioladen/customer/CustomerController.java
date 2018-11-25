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

import java.util.List;

@Controller
@Transactional
public class CustomerController implements ApplicationEventPublisherAware {

	private final CustomerRepository customerRepository;

	CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	/*Functions for register.html*/
	@RequestMapping("/register")
	public String register() {
		return "register";
	}

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
		Customer.Sex customerSex;

		switch (sex) {
			case "male":
				customerSex = Customer.Sex.MALE;
				break;
			case "female":
				customerSex = Customer.Sex.FEMALE;
				break;
			case "various":
				customerSex = Customer.Sex.VARIOUS;
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

		if (firstname.equals("")) {
			model.addAttribute("errorFirstName", true);
			model.addAttribute("errorFirstNameMsg", "Bitte Vornamen angeben");
			return "register";
		} else {
			safeFirstName = firstname;
		}

		if (lastname.equals("")) {
			model.addAttribute("errorLastName", true);
			model.addAttribute("errorLastNameMsg", "Bitte Nachnamen angeben");
			return "register";
		} else {
			safeLastName = lastname;
		}

		if (!customerRepository.findAll().isEmpty()) {
			for (Customer customer : customerRepository.findAll()) {
				if (!customer.getEmail().equals(email)) {
					safeEmail = email;
				} else {
					model.addAttribute("errorEmail", true);
					model.addAttribute("errorEmailMsg", "Diese Email ist bereits vorhanden");
					return "register";
				}
			}
		} else {
			safeEmail = email;
		}

		Customer customer = new Customer(safeFirstName, safeLastName, safeEmail, customerSex, customerType);

		if (!phone.isEmpty()) {
			customer.setPhone(phone);
		}
		if (!address.isEmpty()) {
			customer.setStreet(address);
		}
		customerRepository.save(customer);
		publishEvent(customer, EntityLevel.CREATED);


		return "redirect:/customerlist";
	}

	/* Event publisher */

	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	private void publishEvent(Customer customer, EntityLevel entityLevel) {
		publisher.publishEvent(new EntityEvent<>(customer, entityLevel));
	}

	/*Function for customerlist.html*/

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@GetMapping("/customerlist")
	String customerRepository(Model model) {
		List<Customer> customerList = customerRepository.findAll();
		model.addAttribute("customerList", customerList);

		return "customerlist";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@GetMapping("/customerlist/delete")
	String deleteCustomer(@RequestParam Long id) {
		customerRepository.deleteById(id);

		return "redirect:/customerlist";
	}
}

