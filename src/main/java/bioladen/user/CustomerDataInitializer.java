package bioladen.user;

import java.util.Arrays;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class CustomerDataInitializer {

	private final UserAccountManager userAccountManager;
	private final CustomerRepository customerRepository;

	/**
	 * @param userAccountManager
	 * @param customerRepository
	 */
	CustomerDataInitializer(UserAccountManager userAccountManager, CustomerRepository customerRepository) {

		Assert.notNull(customerRepository, "CustomerRepository must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");

		this.userAccountManager = userAccountManager;
		this.customerRepository = customerRepository;
	}

	/*
	 * (non-Javadoc)
	 * @see org.salespointframework.core.DataInitializer#initialize()
	 */

	//@Override
	public void initialize() {

		// (｡◕‿◕｡)
		// UserAccounts bestehen aus einem Identifier und eine Password, diese werden auch für ein Login gebraucht
		// Zusätzlich kann ein UserAccount noch Rollen bekommen, diese können in den Controllern und im View dazu genutzt
		// werden
		// um bestimmte Bereiche nicht zugänglich zu machen, das "ROLE_"-Prefix ist eine Konvention welche für Spring
		// Security nötig ist.

		// Skip creation if database was already populated
		if (userAccountManager.findByUsername("boss").isPresent()) {
			return;
		}

		UserAccount bossAccount = userAccountManager.create("Feldfreude", "123", Role.of("ROLE_BOSS"));
		userAccountManager.save(bossAccount);

		Role customerRole = Role.of("ROLE_HAUSKUNDE");

		UserAccount ua1 = userAccountManager.create("Laura", "123", customerRole);
		UserAccount ua2 = userAccountManager.create("ChrisPChicken", "123", customerRole);
		UserAccount ua3 = userAccountManager.create("Peter", "123", customerRole);
		UserAccount ua4 = userAccountManager.create("Jürgen", "123", customerRole);

		Customer c1 = new Customer(ua1, "Testweg 1");
		Customer c2 = new Customer(ua2, "Testweg 2");
		Customer c3 = new Customer(ua3, "Testweg 3");
		Customer c4 = new Customer(ua4, "Testweg 4");

		customerRepository.saveAll(Arrays.asList(c1, c2, c3, c4));

		Role staffRole = Role.of("ROLE_PERSONAL");

		UserAccount pa1 = userAccountManager.create("Klaudia", "123", staffRole);
		UserAccount pa2 = userAccountManager.create("Bernt", "123", staffRole);

		Customer p1 = new Customer(pa1, "Feldweg 1");
		Customer p2 = new Customer(pa2, "Feldweg 2");

		customerRepository.saveAll(Arrays.asList(p1, p2));

	}

}
