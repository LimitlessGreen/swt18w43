package bioladen.customer;

import bioladen.datahistory.DataHistoryManager;
import bioladen.datahistory.EntityLevel;
import lombok.RequiredArgsConstructor;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Lisa Riedel
 */
@Service
@RequiredArgsConstructor
public class CustomerManager {

	private final CustomerRepository customerRepository;
	private final UserAccountManager userAccountManager;
	private final AuthenticationManager authenticationManager;

	/*----------------------*/
	/*  1. ADDS
	/*----------------------*/

	/**
	 * Returns all Customer
	 * @return
	 */
	public ArrayList<Customer> getAll() {
		return customerRepository.findAll();
	}

	/**
	 * finds Customer in Repository with id
	 * @param id must not be {@literal null}
	 * @return
	 */
	public Customer get(Long id) {
		return customerRepository.findById(id).orElse(null);
	}

	/*------------------------*/
	/*  2. SAVES
	/*------------------------*/

	/**
	 * Save Customers with Event
	 * @param customerList
	 * @return
	 */
	public Iterable<Customer> saveAll(Iterable<Customer> customerList) {

		Iterable<Customer> customerListTmp = customerRepository.saveAll(customerList);

		for(Customer customer: customerList){
			// (👁 ᴥ 👁) Event
			pushCustomer(customer, EntityLevel.CREATED);
		}

		return customerListTmp;
	}

	/**
	 * Save a Customer with Event
	 * @param customer
	 * @return
	 */
	public Customer save(Customer customer) {
		Customer customerTmp = customerRepository.save(customer);

		// (👁 ᴥ 👁) Event
		pushCustomer(customer, EntityLevel.CREATED);

		return customerTmp;
	}

	/*------------------------*/
	/*  3. DELETIONS
	/*------------------------*/

	/**
	 * Delete Customer with Event
	 * @param id
	 */
	public void delete(Long id) {
		Customer customer = this.get(id);
		customerRepository.deleteById(id);

		// (👁 ᴥ 👁) Event
		pushCustomer(customer, EntityLevel.DELETED);
	}

	/*------------------------*/
	/*  4. MODIFICATIONS
	/*------------------------*/

	/**
	 * Modify Customer with Event
	 * @param customer
	 * @return
	 */
	public Customer modified(Customer customer){

		Customer customerTmp = customerRepository.save(customer);

		// (👁 ᴥ 👁) Event
		pushCustomer(customer, EntityLevel.MODIFIED);

		return customerTmp;
	}

	/*------------------------*/
	/*  5. Types
	/*------------------------*/

	public CustomerType type(String type){
		CustomerType customerType;
		switch (type) {
			case "Manager":
				customerType = CustomerType.MANAGER;
				break;
			case "Staff":
				customerType = CustomerType.STAFF;
				break;
			case "Major":
				customerType = CustomerType.MAJOR_CUSTOMER;
				break;
			case "House":
				customerType = CustomerType.HOUSE_CUSTOMER;
				break;
			default:
				throw new IllegalArgumentException(type);
		}
		return customerType;
	}

	public Sex sex(String sex){
		Sex customerSex;
		switch (sex) {
			case "male":
				customerSex = Sex.MALE;
				break;
			case "female":
				customerSex = Sex.FEMALE;
				break;
			case "various":
				customerSex = Sex.VARIOUS;
				break;
			default:
				throw new IllegalArgumentException(sex);
		}
		return customerSex;
	}

	/*
         _________________
        < Event publisher >
         -----------------
            \   ^__^
             \  (@@)\_______
                (__)\       )\/\
                    ||----w |
                    ||     ||

    */

	private final DataHistoryManager<Customer> dataHistoryManager;

	private void pushCustomer(Customer customer, EntityLevel entityLevel) {
		Optional<UserAccount> currentUser = this.authenticationManager.getCurrentUser();
		dataHistoryManager.push(customer.getName(), customer, entityLevel, currentUser.orElse(null));
	}
}

