package bioladen.order;

import bioladen.product.distributor_product.DistributorProduct;
import lombok.Getter;
import org.salespointframework.quantity.Quantity;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

//@Entity
@Getter
public class OrderItem {

	@GeneratedValue
	@Id
	private Long id;
	private BigDecimal price;
	private Quantity quantity;
	private DistributorProduct product;

	public OrderItem(Quantity quantity, DistributorProduct product) {
		this.quantity = quantity;
		this.product = product;

		this.price = product.getPrice().multiply(quantity.getAmount());
	}
}

