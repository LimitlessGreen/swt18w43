package bioladen.order;

import bioladen.product.distributor_product.DistributorProduct;
import lombok.RequiredArgsConstructor;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Service;

/**
 * Manager to easy manage Objects for Orders
 */
@Service
@RequiredArgsConstructor
public class OrderManager {

	private final DistributorOrderRepository distributorOrderRepository;
	private final OrderItemRepository orderItemRepository;

	/**
	 * Creates a ne OrderItem and stores it in the repository
	 * @param product The DistributorProduct to be stored
	 * @param quantity The amount to be ordered
	 * @return a new stored OrderItem
	 */
	public OrderItem createOrderItem(DistributorProduct product, Quantity quantity) {
		return orderItemRepository.save(new OrderItem(quantity, product));
	}

	/**
	 * Deletes a DistributorOrder
	 * @param order
	 */
	public void delete(DistributorOrder order) {
		distributorOrderRepository.delete(order);
	}

	/**
	 * Creates a DistributorOrder
	 * @param order
	 */
	public void createOrder(DistributorOrder order) {
		distributorOrderRepository.save(order);
	}
}
