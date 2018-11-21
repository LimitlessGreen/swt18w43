package bioladen.customer;

import bioladen.event.EntityEvent;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountIdentifier;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class UserAccEventListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final UserAccountManager userAccountManager;
	private final  CustomerRepository customerRepository;


	UserAccEventListener(UserAccountManager userAccountManager, CustomerRepository customerRepository) {
		this.userAccountManager = userAccountManager;
		this.customerRepository = customerRepository;
	}

	@Async
	@EventListener
	public void listenCustomerEvent(EntityEvent<Customer> event) {
		Customer customer = event.getEntity();

		switch (event.getEventLevel()) {
			case CREATED:
				if (customer.isCustomerType(Customer.CustomerType.STAFF)){
					if(!userAccountManager.findByUsername(customer.getEmail()).isPresent()) {
						UserAccount userAccount = userAccountManager.create(customer.getEmail(), "blattgrün43", Role.of("ROLE_STAFF"));
						userAccountManager.save(userAccount);
						customerRepository.save(customer);
					}
					else {
						userAccountManager.enable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
					}

				}
				else if (customer.isCustomerType(Customer.CustomerType.MANAGER)){
					if (!userAccountManager.findByUsername(customer.getEmail()).isPresent()) {
						UserAccount userAccount = userAccountManager.create(customer.getEmail(), "blattgrün43", Role.of("ROLE_MANAGER"));
						userAccountManager.save(userAccount);
						customerRepository.save(customer);
					}
					else {
						userAccountManager.enable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
					}

				}
				break;
			case DELETED:
				if (customer.isCustomerType(Customer.CustomerType.STAFF)){
					userAccountManager.disable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
				}
				else if (customer.isCustomerType(Customer.CustomerType.MANAGER)){
					userAccountManager.disable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
				}
				break;

			case MODIFIED:
				break; //TODO Customer modified
			default: break;
		}
	}
}
