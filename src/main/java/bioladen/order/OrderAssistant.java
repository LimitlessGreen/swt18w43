package bioladen.order;

import bioladen.event.EntityCreatedEvent;
import bioladen.product.DistributorProduct;
import bioladen.product.Product;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderAssistant {

	private Map<DistributorProduct, Amount> productsToBeOrdered;

	public Map<DistributorProduct, Amount> getAvailable(Product product, Amount amount) {
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
