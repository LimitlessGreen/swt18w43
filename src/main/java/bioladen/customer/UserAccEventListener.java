package bioladen.customer;

import bioladen.event.EntityEvent;
import lombok.RequiredArgsConstructor;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccEventListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final UserAccountManager userAccountManager;

	@Async
	@EventListener
	public void listenCustomerEvent(EntityEvent<Customer> event) {
		Customer customer = event.getEntity();

		switch (event.getEventLevel()) {
			case CREATED:
				if (customer.isCustomerType(CustomerType.STAFF)) {
					this.save(customer, "ROLE_STAFF");
				} else if (customer.isCustomerType(CustomerType.MANAGER)) {
					this.save(customer, "ROLE_MANAGER");
				}
				break;
			case DELETED: //TODO in new Salespoint version delete method: userAccount.delete(userAccountManager.findByUsername(customer.getEmail()).getAll());
				if (customer.isCustomerType(CustomerType.STAFF)) {
					userAccountManager.disable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
				} else if (customer.isCustomerType(CustomerType.MANAGER)) {
					userAccountManager.disable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
				}
				break;

			case MODIFIED:
				if (customer.isCustomerType(CustomerType.STAFF)) {

				} else if (customer.isCustomerType(CustomerType.MANAGER)) {

				}
				break; //TODO Customer modified
			default:
				break;
		}
	}

	private void save(Customer customer, String role) {
		if (!userAccountManager.findByUsername(customer.getEmail()).isPresent()) {
			UserAccount userAccount = userAccountManager.create(customer.getEmail(), "blattgrün43", Role.of(role));
			userAccount.setEmail(customer.getEmail());
			userAccountManager.save(userAccount);
		} else {
			userAccountManager.enable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
		}
	}
}
