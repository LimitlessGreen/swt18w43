package bioladen.customer;

import bioladen.datahistory.DataEntry;
import lombok.RequiredArgsConstructor;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserAccEventListener {

	private final UserAccountManager userAccountManager;

	@EventListener
	public void listenCustomerEvent(DataEntry<Customer> event) {
		Customer customer = event.getEntity();


		switch (event.getEntityLevel()) {
			case CREATED:
				this.create(customer);
				break;
			case DELETED: //new Salespoint delete-method:
				this.delete(customer); //userAccount.delete(userAccountManager.findByUsername(customer.getEmail()).getAll());
				break;

			case MODIFIED:
				//assert oldCustomer != null;
				this.modified(event.getEntityBeforeModified(), customer);
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
			userAccountManager.enable(Objects.requireNonNull(
					userAccountManager.findByUsername(customer.getEmail()).orElse(null)).getId());
		}
	}

	private void delete(Customer customer){
		if (customer.isCustomerType(CustomerType.STAFF)) {
			userAccountManager.disable(Objects.requireNonNull(
					userAccountManager.findByUsername(customer.getEmail()).orElse(null)).getId());
		} else if (customer.isCustomerType(CustomerType.MANAGER)) {
			userAccountManager.disable(userAccountManager.findByUsername(customer.getEmail()).orElse(null).getId());
		}
	}

	private void create(Customer customer){
		if (customer.isCustomerType(CustomerType.STAFF)) {
			this.save(customer, "ROLE_STAFF");
		} else if (customer.isCustomerType(CustomerType.MANAGER)) {
			this.save(customer, "ROLE_MANAGER");
		}
	}

	private void update(Customer oldCustomer, Customer customer,String oldRole, String role){
		UserAccount userAccount = userAccountManager.findByUsername(customer.getEmail()).orElse(null);
		if (userAccount != null) {
			userAccount.remove(Role.of(oldRole));
			userAccount.add(Role.of(role));
			userAccountManager.save(userAccount);
		}

	}

	private<T extends Customer> void modified(T oldCustomer, T customer){
		if (!oldCustomer.getCustomerType().equals(customer.getCustomerType())){
			if (oldCustomer.isCustomerType(CustomerType.STAFF) && customer.isCustomerType(CustomerType.MANAGER)){
				this.update(oldCustomer, customer,"ROLE_STAFF", "ROLE_MANAGER");

			} else if (oldCustomer.isCustomerType(CustomerType.MANAGER) && customer.isCustomerType(CustomerType.STAFF)){
				this.update(oldCustomer, customer,"ROLE_MANAGER", "ROLE_STAFF");
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
