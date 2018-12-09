package bioladen.customer;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
	ArrayList<Customer> findAll();

	Optional<Customer> findByEmail (String email);

}
