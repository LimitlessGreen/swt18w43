package bioladen.customer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

interface CustomerRepository extends CrudRepository<Customer, Long> {
	public ArrayList<Customer> findAll();

}
