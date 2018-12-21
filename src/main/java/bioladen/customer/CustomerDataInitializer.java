package bioladen.customer;

import lombok.RequiredArgsConstructor;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author Lisa Riedel
 */
@Component
@RequiredArgsConstructor
public class CustomerDataInitializer implements DataInitializer {
	private final UserAccountManager userAccountManager;
	private final CustomerManager customerManager;

	/**
	 * Initialized some customer
	 */
	@Override
	public void initialize() {

		if (userAccountManager.findByUsername("feldfreude@bio.de").isPresent()) {
			return;
		}
		Customer manager = new Customer("Flori", "Feldfreude", "feldfreude@bio.de", Sex.MALE, CustomerType.MANAGER);
		manager.setStreet("Feldweg 43, 24242 Felde");

		Customer staff = new Customer("Berta", "Bunt", "bertabunt@bio.de", Sex.FEMALE, CustomerType.STAFF);
		staff.setStreet("Bergstraße 69, 57612 Busenhausen");

		Customer majorCustomer = new Customer("Hilde", "Garten", "garten@obst.de", Sex.FEMALE, CustomerType.MAJOR_CUSTOMER);
		majorCustomer.setStreet("Hof 4, 06862 Hundeluft");
		majorCustomer.setPhone("012412515");

		Customer houseCustomer = new Customer("Manfred", "Jungfleisch", "jungfleisch@manfred.de",
				Sex.MALE, CustomerType.HOUSE_CUSTOMER);
		houseCustomer.setStreet("Holzweg 108, 63589 Linsengericht");
		houseCustomer.setPhone("+49124214214");

		customerManager.saveAll(Arrays.asList(manager, staff));
		customerManager.saveAll(Arrays.asList(majorCustomer,houseCustomer));
	}
}
