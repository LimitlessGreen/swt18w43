package bioladen.order;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository to store OrderItems
 */
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
}
