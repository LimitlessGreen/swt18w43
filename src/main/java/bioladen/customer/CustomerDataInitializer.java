package bioladen.customer;

import java.util.Arrays;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataInitializer implements DataInitializer {
	private final CustomerRepository customerRepository;
	private final UserAccountManager userAccountManager;

	CustomerDataInitializer(UserAccountManager userAccountManager, CustomerRepository customerRepository) {

		this.userAccountManager = userAccountManager;
		this.customerRepository = customerRepository;
	}

	@Override
	public void initialize() {

		if (userAccountManager.findByUsername("feldfreude@bio.de").isPresent()) {
			return;
		}
		Customer manager = new Customer("Flori", "Feldfreude", "feldfreude@bio.de", Sex.MALE, CustomerType.MANAGER);
		manager.setStreet("Feldweg 43, 24242 Felde");
		UserAccount managerAccount = userAccountManager.create("feldfreude@bio.de", "blattgrün43", Role.of("ROLE_MANAGER"));
		userAccountManager.save(managerAccount);

		Customer staff = new Customer("Berta", "Bunt", "bertabunt@bio.de", Sex.FEMALE, CustomerType.STAFF);
		staff.setStreet("Bergstraße 69, 57612 Busenhausen");
		UserAccount staffAccount = userAccountManager.create("bertabunt@bio.de", "blattgrün43", Role.of("ROLE_STAFF"));
		userAccountManager.save(staffAccount);

		customerRepository.saveAll(Arrays.asList(manager, staff));
	}
}
