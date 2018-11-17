package bioladen.product.distributor;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * @author Adrian Kulisch
 */
public interface DistributorRepository extends CrudRepository<Distributor, DistributorIdentifier> {
	public ArrayList<Distributor> findAll();
}
