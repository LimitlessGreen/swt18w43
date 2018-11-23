package bioladen.order;

import bioladen.event.EntityCreatedEvent;
import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.InventoryProduct;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderAssistant {

	private Map<DistributorProduct, Amount> productsToBeOrdered;

	public Map<DistributorProduct, Amount> getAvailable(InventoryProduct inventoryProduct, Amount amount) {
		return productsToBeOrdered; //TODO
	}

	public boolean finishOrder() {
		return false; //TODO
	}

	public void publishOrderCreatedEvent(Order order) {
		//TODO
		new EntityCreatedEvent(order);
	}



}
