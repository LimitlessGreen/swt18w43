package bioladen.customer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.salespointframework.useraccount.AuthenticationManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller
 * @author Lisa Riedel
 */

@Controller
@Transactional
@RequiredArgsConstructor
public class CustomerController {

	private final AuthenticationManager authenticationManager;
	private final CustomerManager customerManager;

	/**
	 * Request for register Customer only for Staff and Manager
	 * @return String
	 */
	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@RequestMapping("/register")
	public String register() {
		return "register";
	}

	/**
	 * Get all params to creat a new {@link Customer}
	 * @param firstname must not be	{@literal null}
	 * @param lastname must not be {@literal null}
	 * @param phone can be {@literal null}
	 * @param email must not be	{@literal null}
	 * @param sex must not be {@literal null}
	 * @param address can be {@literal null}
	 * @param type must not be {@literal null}
	 * @param model only for errors
	 * @return String
	 */
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

		customerSex= customerManager.sex(sex);

		customerType= customerManager.type(type);

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

	/**
	 * Returns all register Customer
	 * @return String
	 */
	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@GetMapping("/customerlist")
	String customerRepository(Model model) {
		model.addAttribute("customerList", customerManager.getAll());

		return "customerlist";
	}

	/**
	 * Delete Customer except himself
	 * @param id
	 * @return
	 */
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

	/**
	 * Provides data for modification
	 * @param id
	 * @param model
	 * @return
	 */
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

	/**
	 * Get modified data
	 * @param firstname must not be	{@literal null}
	 * @param lastname must not be {@literal null}
	 * @param phone can be {@literal null}
	 * @param email must not be	{@literal null}
	 * @param sex must not be {@literal null}
	 * @param address must not be {@literal null}
	 * @param type must not be {@literal null}
	 * @param id must not be {@literal null}
	 * @param model
	 * @return
	 */
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

		customerSex= customerManager.sex(sex);



		if (!authenticationManager.getCurrentUser().get().getUsername().
				equals(customerManager.get(id).getEmail())) {
			customerType= customerManager.type(type);
		} else {
			customerType = customerManager.get(id).getCustomerType();
		}



		String safeFirstName;
		String safeLastName;
		String safeEmail;
		String streetTmp = customerManager.get(id).getStreet();
		String phoneTmp = customerManager.get(id).getPhone();

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

		if (!address.equals(streetTmp) && StringUtils.isNotBlank(address)){
				customer.setStreet(address);
		} else if (address.equals(streetTmp)){
			customer.setStreet(streetTmp);
		}

		if (!phone.equals(phoneTmp) && StringUtils.isNotBlank(phone)){
			customer.setPhone(phone);

		}else if (phone.equals(phoneTmp)){
			customer.setPhone(phoneTmp);
		}

		customer.setId(id);

		customerManager.modified(customer);

		return "redirect:/customerlist";
	}


}

