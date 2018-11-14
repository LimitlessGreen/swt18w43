package bioladen.user;

import org.springframework.data.repository.CrudRepository;

/**
 * A repository interface to manage Customer instances.
 */
interface CustomerRepository extends CrudRepository<Customer, Long> {}
