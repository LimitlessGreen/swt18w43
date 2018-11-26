package bioladen.finances;

import bioladen.customer.Customer;
import bioladen.product.InventoryProduct;
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
	private final static int SCALE = 2;
	private final static int TO_PERCENT = 100;

	private @Getter	final Map<InventoryProduct, CartCartItem> items = new LinkedHashMap<>();
	private @Getter @Setter Customer customer = null;


	/**
	 * Searcher the Map items for the
	 * @param inventoryProduct
	 * If it is found the
	 * @param quantity gets added to the inventoryProduct.
	 * If the inventoryProduct doesn't exist, a new item is added
	 * @return the new CartCartItem
	 */
	public CartCartItem addOrUpdateItem(InventoryProduct inventoryProduct, long quantity) {

		Assert.notNull(inventoryProduct, "InventoryProduct must not be null!");
		Assert.notNull(quantity, "Quantity must not be null!");

		Map<InventoryProduct, CartCartItem> itemsCopy = new LinkedHashMap<>(items);

		for (Map.Entry<InventoryProduct, CartCartItem> e : itemsCopy.entrySet()) {
			if (e.getKey().getProductIdentifier().equals(inventoryProduct.getProductIdentifier())) {
				items.put(e.getKey(), items.get(e.getKey()).add(quantity));
				return e.getValue();
			}
		}
		items.put(inventoryProduct, new CartCartItem(inventoryProduct, quantity));

		return items.get(inventoryProduct);
	}


	/**
	 * Calls addOrUpdateItem with a double amount
	 */
	public CartCartItem addOrUpdateItem(InventoryProduct inventoryProduct, double amount) {
		return addOrUpdateItem(inventoryProduct, (long) amount);
	}


	/**
	 * Removes the item with the
	 * @param identifier from the Map items
	 * @return the removed CartCartItem
	 */
	public Optional<CartCartItem> removeItem(String identifier) {

		Assert.notNull(identifier, "CartItem identifier must not be null!");

		return getItem(identifier) //
				.map(item -> items.remove(item.getInventoryProduct()));
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
	 * clears the ShoppingCart and sets the customer to null.
	 */
	public void clear() {
		customer = null;
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

		for (Map.Entry<InventoryProduct, CartCartItem> e : items.entrySet()) {
			money = money.add(e.getValue().getPrice());
		}

		money = money.multiply(BigDecimal.valueOf(1 - getDiscount()));
		money = money.setScale(SCALE);

		return money;
	}


	/**
	 * @return the price without calculation of discounts etc.
	 */
	public BigDecimal getBasicPrice() {

		BigDecimal money = BigDecimal.valueOf(0);

		for (Map.Entry<InventoryProduct, CartCartItem> e : items.entrySet()) {
			money = money.add(e.getValue().getPrice());
		}

		money = money.setScale(SCALE);

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
	 * @return a formatted customerDiscount for Frontend
	 */
	public String getCustomerDiscountString() {
		return getDiscount() * TO_PERCENT + "%";
	}


	/**
	 * @return the discount if a customer is set, or 0 for a normal customer
	 */
	public double getDiscount() {
		if (customer != null) {
			return Customer.getDiscount(customer.getCustomerType());
		} else {
			return 0;
		}
	}


	@Override
	public Iterator iterator() {
		return items.values().iterator();
	}
}
