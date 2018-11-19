package bioladen.product.distributor;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

/**
 * @author Adrian Kulisch
 */
public interface DistributorRepository extends MongoRepository<Distributor, String> {
	public ArrayList<Distributor> findAll();
}
