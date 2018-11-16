package bioladen.customer;

import org.springframework.data.repository.CrudRepository;

interface CustomerRepository extends CrudRepository<Customer, CustomerIdentifier>{


}
