package bioladen.order;

import bioladen.product.distributor_product.DistributorProduct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Represent a ordered product
 */
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

	/**
	 * Default constructor to create a new instance
	 * @param quantity Amount to be ordered
	 * @param product Product to be ordered
	 */
	public OrderItem(Quantity quantity, DistributorProduct product) {
		this.quantity = quantity;
		this.product = product;


		this.price = product.getPrice().multiply(quantity.getAmount());
	}
}

