package bioladen.customer;

import lombok.RequiredArgsConstructor;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerTools {
	private final UserAccountManager userAccountManager;
	private final CustomerRepository customerRepository;

	/*------------------------*/
	/*  1. CONVERSIONS
	/*------------------------*/

	public UserAccount customerToUser(Customer customer) {
		String email = customer.getEmail();
		return userAccountManager.findByUsername(email).orElse(null);
	}


	public Optional<Customer> userToCustomer (UserAccount user) {
		String email;
		if (user == null) {
			return Optional.empty();
		} else {
			email = user.getUsername();
		}
		return customerRepository.findByEmail(email);
	}
}
