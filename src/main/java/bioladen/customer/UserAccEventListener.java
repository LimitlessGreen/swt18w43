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
	private final CustomerRepository customerRepository;



	@Async
	@EventListener
	public void listenCustomerEvent(EntityEvent<Customer> event) {
		Customer customer = event.getEntity();

		switch (event.getEventLevel()) {
			case CREATED:
				if (customer.isCustomerType(CustomerType.STAFF)) {
					if (!userAccountManager.findByUsername(customer.getEmail()).isPresent()) {
						UserAccount userAccount = userAccountManager.create(customer.getEmail(), "blattgrün43", Role.of("ROLE_STAFF"));
						userAccountManager.save(userAccount);
						customerRepository.save(customer);
					} else {
						userAccountManager.enable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
					}

				} else if (customer.isCustomerType(CustomerType.MANAGER)) {
					if (!userAccountManager.findByUsername(customer.getEmail()).isPresent()) {
						UserAccount userAccount = userAccountManager.create(customer.getEmail(), "blattgrün43", Role.of("ROLE_MANAGER"));
						userAccountManager.save(userAccount);
						customerRepository.save(customer);
					} else {
						userAccountManager.enable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
					}

				}
				break;
			case DELETED: //TODO in new Salespoint version delete method: userAccount.delete(userAccountManager.findByUsername(customer.getEmail()).get());
				if (customer.isCustomerType(CustomerType.STAFF)) {
					userAccountManager.disable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
				} else if (customer.isCustomerType(CustomerType.MANAGER)) {
					userAccountManager.disable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
				}
				break;

			case MODIFIED:
				break; //TODO Customer modified
			default:
				break;
		}
	}
}
