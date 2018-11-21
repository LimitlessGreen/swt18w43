package bioladen.finances;

import bioladen.product.Product;
import lombok.Getter;
import org.salespointframework.quantity.Quantity;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Lukas Petzold
 */
@Getter
public class CartCartItem {

	private @Getter final String id;
	private @Getter final BigDecimal price;
	private @Getter final long quantity;
	private @Getter final Product product;

	/**
	 * Creates a new {@link CartCartItem}.
	 *
	 * @param product must not be {@literal null}.
	 * @param quantity must not be {@literal null}.
	 */
	CartCartItem(Product product, long quantity) {
		this(UUID.randomUUID().toString(), product, quantity);
	}

	/**
	 * Creates a new {@link CartCartItem}.
	 *
	 * @param id must not be {@literal null}.
	 * @param product must not be {@literal null}.
	 * @param quantity must not be {@literal null}.
	 */
	private CartCartItem(String id, Product product, long quantity) {

		Assert.notNull(id, "Identifier must not be null!");
		Assert.notNull(product, "Product must be not null!");
		Assert.notNull(quantity, "Quantity must be not null!");

		this.id = id;
		this.quantity = quantity;
		this.price = product.getPrice().multiply(BigDecimal.valueOf(quantity));
		this.product = product;
	}


	/**
	 * Returns the name of the {@link Product} associated with the {@link CartCartItem}.
	 *
	 * @return
	 */
	public final String getProductName() {
		return product.getName();
	}

	/**
	 * Returns a new {@link CartCartItem} that has the given {@link Quantity} added to the current one.
	 *
	 * @param quantity must not be {@literal null}.
	 * @return
	 */
	final CartCartItem add(long quantity) {

		Assert.notNull(quantity, "Quantity must not be null!");

		quantity += this.quantity;
		return new CartCartItem(this.id, this.product, quantity);
	}
	
}
