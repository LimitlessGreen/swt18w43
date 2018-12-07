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
		Customer oldCustomer = event.getEntityBeforeModified().orElse(null);

		switch (event.getEventLevel()) {
			case CREATED:
				this.create(customer);
				break;
			case DELETED: //new Salespoint delete-method:
				this.delete(customer); //userAccount.delete(userAccountManager.findByUsername(customer.getEmail()).getAll());
				break;

			case MODIFIED:
				this.modified(oldCustomer, customer);
				break;
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

	private void delete(Customer customer){
		if (customer.isCustomerType(CustomerType.STAFF)) {
			userAccountManager.disable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
		} else if (customer.isCustomerType(CustomerType.MANAGER)) {
			userAccountManager.disable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
		}
	}

	private void create(Customer customer){
		if (customer.isCustomerType(CustomerType.STAFF)) {
			this.save(customer, "ROLE_STAFF");
		} else if (customer.isCustomerType(CustomerType.MANAGER)) {
			this.save(customer, "ROLE_MANAGER");
		}
	}

	private void update(Customer oldCustomer, Customer customer, String role){
		if (!userAccountManager.findByUsername(customer.getEmail()).isPresent()) {
			UserAccount userAccount = userAccountManager.create(customer.getEmail(),
					String.valueOf(userAccountManager.findByUsername(oldCustomer.getEmail()).get().getPassword()),
					Role.of(role));
			userAccount.setEmail(customer.getEmail());
			userAccountManager.save(userAccount);
		} else {
			userAccountManager.enable(userAccountManager.findByUsername(customer.getEmail()).get().getId());
		}

	}

	private void modified(Customer oldCustomer, Customer customer){
		if (!oldCustomer.getCustomerType().equals(customer.getCustomerType())){
			if (oldCustomer.isCustomerType(CustomerType.STAFF) && customer.isCustomerType(CustomerType.MANAGER)){
				if(!oldCustomer.getEmail().equals(customer.getEmail())){
					this.update(oldCustomer, customer, "ROLE_MANAGER");
				} else {
					this.update(customer, customer, "ROLE_MANAGER");
				}
			} else if (oldCustomer.isCustomerType(CustomerType.MANAGER) && customer.isCustomerType(CustomerType.STAFF)){
				if(!oldCustomer.getEmail().equals(customer.getEmail())){
					this.update(oldCustomer, customer, "ROLE_STAFF");
				} else {
					this.update(customer, customer, "ROLE_STAFF");
				}
			}
			if ((oldCustomer.isCustomerType(CustomerType.MANAGER) ||
					oldCustomer.isCustomerType(CustomerType.STAFF)) &&
					(customer.isCustomerType(CustomerType.HOUSE_CUSTOMER) ||
							customer.isCustomerType(CustomerType.MAJOR_CUSTOMER))){
				this.delete(oldCustomer);

			} else if ((oldCustomer.isCustomerType(CustomerType.HOUSE_CUSTOMER) ||
					oldCustomer.isCustomerType(CustomerType.MAJOR_CUSTOMER)) &&
					(customer.isCustomerType(CustomerType.MANAGER) ||
							customer.isCustomerType(CustomerType.STAFF))){
				this.create(customer);
			}
		}
	}
}
