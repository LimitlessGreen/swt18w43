package bioladen.product.distributor_product;

import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

public interface DistributorProductCatalog extends MongoRepository<DistributorProduct, ProductIdentifier> {
	public ArrayList<DistributorProduct> findAll();
}
