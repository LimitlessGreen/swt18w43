package bioladen.order;

import bioladen.product.distributor_product.DistributorProduct;
import lombok.Getter;
import org.javamoney.moneta.Money;
import org.salespointframework.quantity.Quantity;
import org.springframework.util.Assert;

import javax.money.MonetaryAmount;
import java.util.UUID;

@Getter
public class OrderCartItem {

	private final String id;
	private final MonetaryAmount price;
	private final Quantity quantity;
	private final DistributorProduct product;


	public OrderCartItem(DistributorProduct product, Quantity quantity) {
		this(UUID.randomUUID().toString(), quantity, product);
	}

	private OrderCartItem(String id, Quantity quantity, DistributorProduct product) {

		this.id = id;
		this.quantity = quantity;
		this.product = product;

		this.price = Money.of(product.getPrice().doubleValue() * quantity.getAmount().signum(), "EUR");

	}


	public final String getProductName() {
		return product.getName();
	}

	final OrderCartItem add(Quantity quantity) {

		Assert.notNull(quantity, "Quantity must not be null!");

		return new OrderCartItem(this.id, this.quantity.add(quantity), this.product);
	}

}
