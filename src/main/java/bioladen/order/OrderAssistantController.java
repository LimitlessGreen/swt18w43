package bioladen.order;

import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class OrderAssistantController {

	private OrderManager<Order> orderManager;

	public OrderAssistantController(OrderManager<Order> orderManager) {
		this.orderManager = orderManager;
	}

	public String search(Model model) {
		return "";
	}

	public String assistant(Model model) {
		return "";
	}

	public String addProduct(Model model) { //TODO
		return "";
	}

	public String removeProduct(Model model) {
		return "";
	}

	public OrderAssistant initializeOrderAssistant() {
		return null;
	}


}
