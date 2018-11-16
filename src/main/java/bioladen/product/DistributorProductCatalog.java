package bioladen.product;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface DistributorProductCatalog extends CrudRepository<DistributorProduct, DistributorProductIdentifier> {
}
