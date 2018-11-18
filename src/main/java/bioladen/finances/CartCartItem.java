package bioladen.finances;

import bioladen.product.Product;
import lombok.Getter;
import org.salespointframework.quantity.Quantity;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class CartCartItem  {

	private @Getter final String id;
	private @Getter final BigDecimal price;
	private @Getter final Quantity amount;
	private @Getter	final Product product;

	public CartCartItem(Product product, Quantity amount){
		this(UUID.randomUUID().toString(), amount, product);
	}

	public CartCartItem(String id, Quantity amount, Product product) {

		this.id = id;
		this.amount = amount;
		this.product = product;
		this.price = product.getPrice();


	}


	public final String getProductName() {
		return product.getName();
	}


	final CartCartItem add(Quantity quantity) {

		Assert.notNull(quantity, "Quantity must not be null!");

		return new CartCartItem(this.id, this.amount, this.product);
	}
}
