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

@Service
@RequiredArgsConstructor
public class CustomerManager {

	private final CustomerRepository customerRepository;
	private final UserAccountManager userAccountManager;
	private final AuthenticationManager authenticationManager;

	/*----------------------*/
	/*  1. ADDS
	/*----------------------*/

	public ArrayList<? extends Customer> getAll() {
		return customerRepository.findAll();
	}

	public Customer get(Long id) {
		return customerRepository.findById(id).orElse(null);
	}

	/*------------------------*/
	/*  2. SAVES
	/*------------------------*/

	public <S extends Customer> Iterable<S> saveAll(Iterable<S> customerList) {

		Iterable<S> customerListTmp = customerRepository.saveAll(customerList);

		for(S customer: customerList){
			// (👁 ᴥ 👁) Event
			pushCustomer(customer, EntityLevel.CREATED);
		}

		return customerListTmp;
	}

	public <S extends Customer> S save(S customer) {
		S customerTmp = customerRepository.save(customer);

		// (👁 ᴥ 👁) Event
		pushCustomer(customer, EntityLevel.CREATED);

		return customerTmp;
	}

	/*------------------------*/
	/*  3. DELETIONS
	/*------------------------*/

	public void delete(Long id) {
		Customer customer = this.get(id);
		customerRepository.deleteById(id);

		// (👁 ᴥ 👁) Event
		pushCustomer(customer, EntityLevel.DELETED);
	}

	/*------------------------*/
	/*  4. MODIFICATIONS
	/*------------------------*/

	public <S extends Customer> S modified(S customer){
		S customerTmp = customerRepository.save(customer);

		// (👁 ᴥ 👁) Event
		pushCustomer(customer, EntityLevel.MODIFIED);

		return customerTmp;
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

