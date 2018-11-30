package bioladen.order;

import bioladen.product.distributor_product.DistributorProduct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem {

	@GeneratedValue
	@Id
	private Long id;
	private BigDecimal price;
	private Quantity quantity;
	@ManyToOne
	private DistributorProduct product;

	public OrderItem(Quantity quantity, DistributorProduct product) {
		this.quantity = quantity;
		this.product = product;


		this.price = product.getPrice().multiply(quantity.getAmount());
	}
}

