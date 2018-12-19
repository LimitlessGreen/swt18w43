package bioladen.customer;

import bioladen.datahistory.DataEntry;
import lombok.RequiredArgsConstructor;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 *@author  Lisa Riedel
 */
@Service
@RequiredArgsConstructor
public class UserAccEventListener {

	private final UserAccountManager userAccountManager;

	/**
	 * listen to Events from Customer and adjusts the associated accounts
	 * @param event must not be	{@literal null}
	 */
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

	/**
	 * save oder disable an existing account
	 * @param customer must not be	{@literal null}
	 * @param role must not be	{@literal null}
	 */

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

	/**
	 * Disable an existing account
	 * @param customer must not be	{@literal null}
	 */
	private void delete(Customer customer){
		if (customer.isCustomerType(CustomerType.STAFF)) {
			userAccountManager.disable(Objects.requireNonNull(
					userAccountManager.findByUsername(customer.getEmail()).orElse(null)).getId());
		} else if (customer.isCustomerType(CustomerType.MANAGER)) {
			userAccountManager.disable(userAccountManager.findByUsername(customer.getEmail()).orElse(null).getId());
		}
	}

	/**
	 * Create an account with role
	 * @param customer must not be	{@literal null}
	 */
	private void create(Customer customer){
		if (customer.isCustomerType(CustomerType.STAFF)) {
			this.save(customer, "ROLE_STAFF");
		} else if (customer.isCustomerType(CustomerType.MANAGER)) {
			this.save(customer, "ROLE_MANAGER");
		}
	}

	/**
	 * Update role
	 * @param customer must not be	{@literal null}
	 * @param oldRole must not be	{@literal null}
	 * @param role must not be	{@literal null}
	 */
	private void update(Customer customer,String oldRole, String role){
		UserAccount userAccount = userAccountManager.findByUsername(customer.getEmail()).orElse(null);
		if (userAccount != null) {
			userAccount.remove(Role.of(oldRole));
			userAccount.add(Role.of(role));
			userAccountManager.save(userAccount);
		}

	}

	/**
	 * arranges the types of modification
	 * @param oldCustomer must not be	{@literal null}
	 * @param customer must not be	{@literal null}
	 */
	private void modified(Customer oldCustomer, Customer customer){
		if (!oldCustomer.getCustomerType().equals(customer.getCustomerType())){
			if (oldCustomer.isCustomerType(CustomerType.STAFF) && customer.isCustomerType(CustomerType.MANAGER)){
				this.update(customer,"ROLE_STAFF", "ROLE_MANAGER");

			} else if (oldCustomer.isCustomerType(CustomerType.MANAGER) && customer.isCustomerType(CustomerType.STAFF)){
				this.update(customer,"ROLE_MANAGER", "ROLE_STAFF");
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
