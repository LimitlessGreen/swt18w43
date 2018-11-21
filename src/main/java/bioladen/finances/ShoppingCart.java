package bioladen.finances;

import bioladen.product.Product;
import lombok.Getter;
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

	private @Getter	final Map<Product, CartCartItem> items = new LinkedHashMap<>();

	public CartCartItem addOrUpdateItem(Product product, long quantity) {

		Assert.notNull(product, "Product must not be null!");
		Assert.notNull(quantity, "Quantity must not be null!");

		Map<Product, CartCartItem> itemsCopy = new LinkedHashMap<>(items);

		for (Map.Entry<Product, CartCartItem> e : itemsCopy.entrySet()) {
			if (e.getKey().getProductIdentifier().equals(product.getProductIdentifier())) {
				items.put(e.getKey(), items.get(e.getKey()).add(quantity));
				return e.getValue();
			}
		}
		items.put(product, new CartCartItem(product, quantity));

		return items.get(product);
	}



	public CartCartItem addOrUpdateItem(Product product, double amount) {
		return addOrUpdateItem(product, (long) amount);
	}


	public Optional<CartCartItem> removeItem(String identifier) {

		Assert.notNull(identifier, "CartItem identifier must not be null!");

		return getItem(identifier) //
				.map(item -> items.remove(item.getProduct()));
	}


	public Optional<CartCartItem> getItem(String identifier) {

		Assert.notNull(identifier, "CartCartItem identifier must not be null!");

		return items.values().stream() //
				.filter(item -> item.getId().equals(identifier)) //
				.findFirst();
	}


	public void clear() {
		items.clear();
	}


	public boolean isEmpty() {
		return items.isEmpty();
	}


	public BigDecimal getPrice() {

		BigDecimal money = BigDecimal.valueOf(0);

		for (Map.Entry<Product, CartCartItem> e : items.entrySet()) {
			money = money.add(e.getValue().getPrice());
			money = money.setScale(2);
		}

		return money;
	}



	@Override
	public Iterator iterator() {
		return items.values().iterator();
	}
}
