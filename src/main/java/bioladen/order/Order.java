package bioladen.order;

import bioladen.product.distributor.Distributor;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
public class Order {

	@GeneratedValue
	@Id
	private Long id;

	private Distributor distributor;
	@ManyToOne
	private UserAccount userAccount;

	@OneToMany
	private List<OrderItem> items = new ArrayList<>();

	public Order(UserAccount userAccount, Distributor distributor) {
		this.userAccount = userAccount;
		this.distributor = distributor;
	}

	public void addItems( OrderCart orderCart ) {
		for (OrderCartItem item : orderCart) {
			items.add(new OrderItem(item.getQuantity(), item.getProduct()));
		}
		orderCart.clear();
	}

}
