package bioladen.product.distributor_product;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * @author Adrian Kulisch
 */
public interface DistributorProductCatalog extends CrudRepository<DistributorProduct, Long> {
	public ArrayList<DistributorProduct> findAll();
}
