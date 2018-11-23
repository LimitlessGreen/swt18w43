package bioladen.customer;

import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Controller;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.util.Streamable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
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
							  @RequestParam("lastname") String lastname,
							  @RequestParam("phone") String phone,
							  @RequestParam("email") String email,
							  @RequestParam("address") String address,
							  @RequestParam("sex") String sex,
							  @RequestParam("type") String type){

		Customer.CustomerType customerType;
		Customer.Sex customerSex;

		switch (sex) {
			case "male": customerSex = Customer.Sex.MALE; break;
			case "female": customerSex = Customer.Sex.FEMALE; break;
			case "various": customerSex = Customer.Sex.VARIOUS; break;
			default: throw new IllegalArgumentException(sex);
		}

		switch (type) {
			case "Manager": customerType = Customer.CustomerType.MANAGER; break;
			case "Staff": customerType = Customer.CustomerType.STAFF; break;
			case "Major": customerType = Customer.CustomerType.MAJOR_CUSTOMER; break;
			case "House": customerType = Customer.CustomerType.HOUSE_CUSTOMER; break;
			default: throw new  IllegalArgumentException(type);
		}

		Customer customer = new Customer(firstname, lastname, email, customerSex, customerType);

		if (!phone.isEmpty()) { customer.setPhone(phone);}
		if (!address.isEmpty()) { customer.setStreet(address);}
		customerRepository.save(customer);
		publishEvent(customer, EntityLevel.CREATED);


		return "register";
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

	@GetMapping("/customerlist")
	String customerRepository(Model model) {
		List<Customer> customerList = customerRepository.findAll();
		model.addAttribute("customerList", customerList);

		return "customerlist";
	}

	@GetMapping("/customerlist/{id}") //TODO string id to long
	String deleteCustomer(@PathVariable String id) {
		customerRepository.deleteById(id);

		return "redirect:/customerlist";
	}
}

