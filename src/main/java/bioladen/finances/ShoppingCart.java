package bioladen.finances;

import bioladen.customer.Customer;
import bioladen.product.Product;
import lombok.Getter;
import lombok.Setter;
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
	private @Getter @Setter Customer customer;
	private @Getter @Setter double customerDiscount = 1;


	/**
	 * Searcher the Map items for the
	 * @param product
	 * If it is found the
	 * @param quantity gets added to the product.
	 * If the product doesn't exist, a new item is added
	 * @return the new CartCartItem
	 */
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


	/**
	 * Calls addOrUpdateItem with a double amount
	 */
	public CartCartItem addOrUpdateItem(Product product, double amount) {
		return addOrUpdateItem(product, (long) amount);
	}


	/**
	 * Removes the item with the
	 * @param identifier from the Map items
	 * @return the removed CartCartItem
	 */
	public Optional<CartCartItem> removeItem(String identifier) {

		Assert.notNull(identifier, "CartItem identifier must not be null!");

		return getItem(identifier) //
				.map(item -> items.remove(item.getProduct()));
	}


	/**
	 * @return the item with the
	 * @param identifier
	 */
	public Optional<CartCartItem> getItem(String identifier) {

		Assert.notNull(identifier, "CartCartItem identifier must not be null!");

		return items.values().stream() //
				.filter(item -> item.getId().equals(identifier)) //
				.findFirst();
	}


	/**
	 * clears the ShoppingCart
	 */
	public void clear() {
		items.clear();
	}


	/**
	 * Checks if the ShoppingCart is empty
	 */
	public boolean isEmpty() {
		return items.isEmpty();
	}


	/**
	 * @return the sum of the ShoppingCart
	 * Calculates with the userDiscount
	 */
	public BigDecimal getPrice() {

		BigDecimal money = BigDecimal.valueOf(0);

		for (Map.Entry<Product, CartCartItem> e : items.entrySet()) {
			money = money.add(e.getValue().getPrice());
		}

		money = money.multiply(BigDecimal.valueOf(1 - customerDiscount));
		money = money.setScale(2);

		return money;
	}

	public BigDecimal getBasicPrice() {

		BigDecimal money = BigDecimal.valueOf(0);

		for (Map.Entry<Product, CartCartItem> e : items.entrySet()) {
			money = money.add(e.getValue().getPrice());
		}

		money = money.setScale(2);

		return money;
	}


	/**
	 * @return the amount of items in the ShoppingCart
	 */
	public int getAmountOfItems() {
		int amount = items.size();

		return amount;
	}


	/**
	 * @return whether or not customer is set
	 */
	public boolean hasCustomer() {
		try {
			if (customer != null) {
				return true;
			} else {
				return false;
			}
		}
		catch (Exception e) {
			return false;
		}
	}


	/**
	 * @return a formatted customerDiscount for Frontend
	 */
	public String getCustomerDiscountString() {
		String customerDiscountString = customerDiscount * 100 + "%";

		return customerDiscountString;
	}


	/**
	 * @return whether or not the customerDiscount is 1
	 */
	public boolean checkDiscount() {
		return customerDiscount == 1;
	}


	@Override
	public Iterator iterator() {
		return items.values().iterator();
	}
}
