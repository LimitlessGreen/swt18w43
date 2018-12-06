package bioladen.customer;

import org.apache.commons.lang3.StringUtils;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.salespointframework.useraccount.AuthenticationManager;

@Controller
@Transactional
@RequiredArgsConstructor
public class CustomerController {

	private final AuthenticationManager authenticationManager;
	private final CustomerManager customerManager;

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

		if (StringUtils.isBlank(firstname)) {
			model.addAttribute("errorRegister", true);
			model.addAttribute("errorRegisterMsg", "Pflichtfelder wurde nicht aufgefüllt.");
			return "register";
		} else {
			safeFirstName = firstname;
		}

		if (StringUtils.isBlank(lastname)) {
			model.addAttribute("errorRegister", true);
			model.addAttribute("errorRegisterMsg", "Pflichtfelder wurde nicht aufgefüllt.");
			return "register";
		} else {
			safeLastName = lastname;
		}

		if (StringUtils.isBlank(email)) {
			model.addAttribute("errorRegister", true);
			model.addAttribute("errorRegisterMsg", "Pflichtfelder wurde nicht aufgefüllt.");
			return "register";
		} else {
			if (!customerManager.getAll().isEmpty()) {
				for (Customer customer : customerManager.getAll()) {
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

		if (StringUtils.isNotBlank(phone)) {
			customer.setPhone(phone);
		}
		if (StringUtils.isNotBlank(address)) {
			customer.setStreet(address);
		}
		customerManager.save(customer);

		model.addAttribute("successRegister", true);
		return "register";
	}

	/*Function for customerlist.html*/

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@GetMapping("/customerlist")
	String customerRepository(Model model) {
		model.addAttribute("customerList", customerManager.getAll());

		return "customerlist";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/customerlist/delete")
	String deleteCustomer(@RequestParam Long id) {
		Customer customer = customerManager.get(id);
		if (!authenticationManager.getCurrentUser().get().getUsername().
				equals(customerManager.get(id).getEmail())) {
			customerManager.delete(id);

		}


		return "redirect:/customerlist";
	}


	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/customerlist/modify")
	String modifyCustomer(@RequestParam Long id, Model model) {
		Customer customer = customerManager.get(id);
		model.addAttribute("firstname", customerManager.get(id).getFirstname());
		model.addAttribute("lastname", customerManager.get(id).getLastname());
		model.addAttribute("email", customerManager.get(id).getEmail());
		model.addAttribute("street", customerManager.get(id).getStreet());
		model.addAttribute("phone", customerManager.get(id).getPhone());
		model.addAttribute("sex", customerManager.get(id).getSex().getSexName());
		model.addAttribute("type", customerManager.get(id).getCustomerType().getTypeName());
		model.addAttribute("id", customerManager.get(id).getId());

		return "modifycustomer";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@PostMapping("/modified")
	String modifiedCustomer(@RequestParam("firstname") String firstname,
							@RequestParam("lastname")  String lastname,
							@RequestParam("phone")     String phone,
							@RequestParam("email")     String email,
							@RequestParam("sex")       String sex,
							@RequestParam("address")   String address,
							@RequestParam("type")      String type,
							@RequestParam("id")		   Long id,

							Model model){
		CustomerType customerType;
		Sex customerSex;

		switch (sex) {
			case "männlich":
				customerSex = Sex.MALE;
				break;
			case "weiblich":
				customerSex = Sex.FEMALE;
				break;
			case "divers":
				customerSex = Sex.VARIOUS;
				break;
			default:
				throw new IllegalArgumentException(sex);
		}

		switch (type) {
			case "Manager":
				customerType = CustomerType.MANAGER;
				break;
			case "Personal":
				customerType = CustomerType.STAFF;
				break;
			case "Großkunde":
				customerType = CustomerType.MAJOR_CUSTOMER;
				break;
			case "Hauskunde":
				customerType = CustomerType.HOUSE_CUSTOMER;
				break;
			default:
				throw new IllegalArgumentException(type);
		}


		String safeFirstName;
		String safeLastName;
		String safeEmail;

		if (!firstname.equals(customerManager.get(id).getFirstname())){
			safeFirstName = firstname;
		} else {
			safeFirstName = customerManager.get(id).getFirstname();
		}
		if (!lastname.equals(customerManager.get(id).getLastname())){
			safeLastName = lastname;
		} else {
			safeLastName = customerManager.get(id).getLastname();
		}
		if (!email.equals(customerManager.get(id).getEmail())){
			safeEmail = email;
		} else {
			safeEmail = customerManager.get(id).getEmail();
		}
		 Customer customer = new Customer(safeFirstName,safeLastName, safeEmail, customerSex, customerType);

		if (!address.equals(customerManager.get(id).getStreet())){
			if (StringUtils.isNotBlank(address)){
				customer.setStreet(address);
			} else {
				customer.setStreet(null);
			}
		}
		if (!phone.equals(customerManager.get(id).getPhone()) && StringUtils.isNotBlank(phone)){
			if (StringUtils.isNotBlank(phone)){
				customer.setPhone(phone);
			} else {
				customer.setPhone(null);
			}
		}

		customer.setId(id);

		customerManager.modified(customer);

		return "redirect:/customerlist";
	}


}

