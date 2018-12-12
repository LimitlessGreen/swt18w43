package bioladen.product;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * @author Adrian Kulisch
 */
public interface InventoryProductCatalog extends CrudRepository<InventoryProduct, Long> {
	ArrayList<InventoryProduct> findAll();

	InventoryProduct findByName(String name);

}
