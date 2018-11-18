package bioladen.finances;

import bioladen.product.Product;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;


import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Lukas Petzold
 */
public class ShoppingCart implements Streamable<CartCartItem> {

	private final Map<Product, CartCartItem> items = new LinkedHashMap<>();


	public BigDecimal getPrice() {

		return items.values().stream() //
				.map(CartCartItem::getPrice) //
				.reduce(BigDecimal::add) //
				.orElse(BigDecimal.valueOf(0));
	}

	public CartCartItem addOrUpdateItem(Product product, Quantity quantity) {

		Assert.notNull(product, "Product must not be null!");
		Assert.notNull(quantity, "Quantity must not be null!");

		return this.items.compute(product, //
				(it, item) -> item == null //
						? new CartCartItem(it, quantity) //
						: item.add(quantity));
	}


	public CartCartItem addOrUpdateItem(Product product, long amount) {
		return addOrUpdateItem(product, Quantity.of(amount));
	}


	public CartCartItem addOrUpdateItem(Product product, double amount) {
		return addOrUpdateItem(product, Quantity.of(amount));
	}

	public Optional<CartCartItem> removeItem(String identifier) {

		Assert.notNull(identifier, "CartItem identifier must not be null!");

		return getItem(identifier) //
				.map(item -> items.remove(item.getProduct()));
	}

	public Optional<CartCartItem> getItem(String identifier) {

		Assert.notNull(identifier, "OrderCartItem identifier must not be null!");


		return items.values().stream() //
				.filter(item -> item.getId().equals(identifier)) //
				.findFirst();
	}



	@Override
	public Iterator iterator() {
		return items.values().iterator();
	}


	public boolean isEmpty() {
		return items.isEmpty();
	}

}
