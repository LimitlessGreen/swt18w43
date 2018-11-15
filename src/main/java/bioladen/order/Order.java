package bioladen.order;

import bioladen.product.distributor.Distributor;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.ManyToOne;

public class Order extends org.salespointframework.order.Order {

	@ManyToOne
	private Distributor distributor;

	public Order(UserAccount userAccount, Distributor distributor) {
		super(userAccount);
		this.distributor = distributor;
	}

	public Order order;


}
