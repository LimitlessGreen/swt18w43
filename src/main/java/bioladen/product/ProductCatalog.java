package bioladen.product;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

/**
 * @author Adrian Kulisch
 */
public interface ProductCatalog extends MongoRepository<Product, String> {
	public ArrayList<Product> findAll();
}
