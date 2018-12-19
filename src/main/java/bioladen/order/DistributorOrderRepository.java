package bioladen.order;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository to store DistributorOrders
 */
public interface DistributorOrderRepository extends CrudRepository<bioladen.order.DistributorOrder, Long> {
}
