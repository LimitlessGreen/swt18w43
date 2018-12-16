package bioladen.customer;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Repository for Customers
 * @author Lisa Riedel
 */

public interface CustomerRepository extends CrudRepository<Customer, Long> {
	ArrayList<Customer> findAll();

	Optional<Customer> findByEmail (String email);

}
