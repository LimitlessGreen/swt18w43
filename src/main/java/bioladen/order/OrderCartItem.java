package bioladen.order;

import bioladen.product.distributor_product.DistributorProduct;
import lombok.Getter;
import org.javamoney.moneta.Money;
import org.salespointframework.quantity.Quantity;
import org.springframework.util.Assert;

import javax.money.MonetaryAmount;
import java.util.UUID;

/**
 * Class to store a single entry of {@link OrderCart}
 */
@Getter
public class OrderCartItem {

	private final String id;
	private final MonetaryAmount price;
	private final Quantity quantity;
	private final DistributorProduct product;


	/**
	 * Public constructor to create a new instance
	 * @param product {@link DistributorProduct} to be contained
	 * @param quantity the Amount
	 */
	public OrderCartItem(DistributorProduct product, Quantity quantity) {
		this(UUID.randomUUID().toString(), quantity, product);
	}

	/**
	 * Internal constructor with a given id
	 * @param id
	 * @param quantity
	 * @param product
	 */
	private OrderCartItem(String id, Quantity quantity, DistributorProduct product) {

		this.id = id;
		this.quantity = quantity;
		this.product = product;

		this.price = Money.of(product.getPrice().doubleValue() * quantity.getAmount().signum(), "EUR");

	}

	/**
	 * Returns the product name
	 * @return product name
	 */
	public final String getProductName() {
		return product.getName();
	}

	/**
	 * Generates a new item with the added amount
	 * @param quantity amount to add
	 * @return the new item
	 */
	final OrderCartItem add(Quantity quantity) {
		if (quantity == null){
			throw new IllegalArgumentException("Quantity must not be null!");
		}

		return new OrderCartItem(this.id, this.quantity.add(quantity), this.product);
	}

}
