package bioladen.product.distributor_product;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

/**
 * @author Adrian Kulisch
 */
public interface DistributorProductCatalog extends MongoRepository<DistributorProduct, String> {
	//public ArrayList<DistributorProduct> findAll();
}
