package bioladen.customer;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

interface CustomerRepository extends CrudRepository<Customer, CustomerIdentifier>{
	public ArrayList<Customer> findAll();

}
