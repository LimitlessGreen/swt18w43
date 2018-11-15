package bioladen.order;

import org.salespointframework.order.OrderManager;
import org.springframework.ui.Model;

public class OrderController {

	private OrderManager<Order> orderOrderManager;

	public OrderController(OrderManager<Order> orderOrderManager){
		this.orderOrderManager = orderOrderManager;
	}

	public String oders(Model model){
		return "";
	}

	public void removeOrder(Model model){
		return;
	}


}
