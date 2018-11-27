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
 * A ShoppingCart where Products can be stored for a sale process.
 *
 * @author Lukas Petzold
 */
public class ShoppingCart implements Streamable<CartCartItem> {
	private final static int SCALE = 2;
	private final static int TO_PERCENT = 100;

	private @Getter	final Map<InventoryProduct, CartCartItem> items = new LinkedHashMap<>();
	private @Getter @Setter Customer customer = null;


	/**
	 * Add an item to the shoppingCart or updates the quantity.
	 *
	 * @param inventoryProduct The map {@link} items gets searched for the inventoryProduct.
	 * @param quantity If it is found, the quantity gets added to the existing quantity of the item.
	 * If the inventoryProduct doesn't exist, a new item is added.
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
	 * Calls addOrUpdateItem with a double amount.
	 */
	public CartCartItem addOrUpdateItem(InventoryProduct inventoryProduct, double amount) {
		return addOrUpdateItem(inventoryProduct, (long) amount);
	}


	/**
	 * Removes an item from the shoppingCart.
	 *
	 * @param identifier Searches the Map {@link} items for the item with the identifier.
	 * @return the removed CartCartItem.
	 */
	public Optional<CartCartItem> removeItem(String identifier) {

		Assert.notNull(identifier, "CartItem identifier must not be null!");

		return getItem(identifier) //
				.map(item -> items.remove(item.getInventoryProduct()));
	}


	/**
	 * Get an item from the shoppingCart.
	 *
	 * @param identifier The Map {@link} items gets searched for the item with the identifier.
	 * @return the CartCartItem.
	 */
	public Optional<CartCartItem> getItem(String identifier) {

		Assert.notNull(identifier, "CartCartItem identifier must not be null!");

		return items.values().stream() //
				.filter(item -> item.getId().equals(identifier)) //
				.findFirst();
	}


	/**
	 * Clears the ShoppingCart and sets the customer to null.
	 */
	public void clear() {
		customer = null;
		items.clear();
	}


	/**
	 * Checks if the ShoppingCart is empty.
	 */
	public boolean isEmpty() {
		return items.isEmpty();
	}


	/**
	 * Get the sum of the shoppingCart with the userDiscount factored in.
	 *
	 * @return the sum of the shoppingCart.
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
	 * Get the sum of the shoppingCart without the userDiscount.
	 *
	 * @return the sum of the shoppingCart.
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
	 * Get the amount of items in the shoppingCart.
	 */
	public int getAmountOfItems() {
		int amount = items.size();

		return amount;
	}


	/**
	 * @return a formatted customerDiscount String for Frontend
	 */
	public String getCustomerDiscountString() {
		return getDiscount() * TO_PERCENT + "%";
	}


	/**
	 * Get the discount for the customer.
	 * If it is not set, a 0 is returned.
	 *
	 * @return the customerDiscount.
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
