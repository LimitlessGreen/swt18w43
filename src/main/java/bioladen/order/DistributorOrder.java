package bioladen.order;

import bioladen.product.distributor.Distributor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Order to get new products from a distributor
 */
@Entity
@Table(name = "DISTRIBUTOR_ORDERS")
@Getter
@NoArgsConstructor
public class DistributorOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@NonNull
	private Distributor distributor;

	@ManyToOne
	@NonNull
	private UserAccount userAccount;

	@OneToMany
	@NonNull
	private List<OrderItem> items = new ArrayList<>();

	/**
	 * Constructor to create a new Order
	 * @param userAccount {@link UserAccount} creating the order
	 * @param distributor {@link Distributor} which delivers the products
	 */
	public DistributorOrder(UserAccount userAccount, Distributor distributor) {
		this.userAccount = userAccount;
		this.distributor = distributor;
	}


	/**
	 * Adds items from a cart to the order and clears the cart afterwards
	 *
	 * @param orderCart Cart containing the items to be added
	 * @param orderManager Manager to store the orderitems with
	 */
	public void addItems(OrderCart orderCart, OrderManager orderManager ) {
		for (OrderCartItem item : orderCart) {
			items.add(orderManager.createOrderItem( item.getProduct(), item.getQuantity()));
		}
		orderCart.clear();
	}

	/**
	 * Calculates the total price
	 *
	 * @return the total price of the order
	 */
	public MonetaryAmount getPrice() {
		MonetaryAmount price = Money.of(0, "EUR");

		for (OrderItem item : items) {
			price = price.add(Money.of(item.getPrice(), "EUR"));

		}

		return price;
	}
}
