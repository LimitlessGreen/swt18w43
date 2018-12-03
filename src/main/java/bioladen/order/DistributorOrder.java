package bioladen.order;

import bioladen.product.distributor.Distributor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

	public DistributorOrder(UserAccount userAccount, Distributor distributor) {
		this.userAccount = userAccount;
		this.distributor = distributor;
	}

	public void addItems(OrderCart orderCart, OrderItemRepository itemRepository) {
		for (OrderCartItem item : orderCart) {
			items.add(new OrderItem(item.getQuantity(), item.getProduct()));
		}
		itemRepository.saveAll(items);
		orderCart.clear();
	}



}
