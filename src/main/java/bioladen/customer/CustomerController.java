package bioladen.customer;

import org.springframework.stereotype.Controller;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Controller
public class CustomerController {

	private final CustomerRepository customerRepository;

	CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

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


		return "register";
	}
}

