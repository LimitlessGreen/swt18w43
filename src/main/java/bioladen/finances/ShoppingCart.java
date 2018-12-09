package bioladen.finances;

import bioladen.customer.Customer;
import bioladen.datahistory.RawEntry;
import bioladen.product.InventoryProduct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


/**
 * A ShoppingCart where Products can be stored for a sale process.
 *
 * @author Lukas Petzold
 */
public class ShoppingCart implements Streamable<CartCartItem>, RawEntry {
	private final static int SCALE = 2;
	private final static int TO_PERCENT = 100;

	private @Getter	final Map<InventoryProduct, CartCartItem> items = new LinkedHashMap<>();
	private @Getter final Map<BigDecimal, Long> pfand = new LinkedHashMap<>();

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
			if (e.getKey().getId().equals(inventoryProduct.getId())) {
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
		pfand.clear();
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
			money = money.add((e.getValue().getPrice()));							// price of CartCartItem
			if(e.getKey().getPfandPrice() != null) {
				money = money.add(e.getKey().getPfandPrice()
						.multiply(BigDecimal.valueOf(e.getValue().getQuantity())));        // pfand added
			}
		}

		money = money.multiply(BigDecimal.valueOf(1 - getDiscount()));				// discount
		money = money.add(getPfandMoney());											// minus pfand
		money = money.add(getMwstMoney());											// plus MwSt.
		money = money.setScale(SCALE, RoundingMode.HALF_EVEN);						// round to 2 decimals after comma

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
			if(e.getKey().getPfandPrice() != null) {
				money = money.add(e.getKey().getPfandPrice().multiply(BigDecimal.valueOf(e.getValue().getQuantity())));
			}
		}

		money = money.setScale(SCALE, RoundingMode.HALF_EVEN);

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
			return customer.getCustomerType().getDiscount();
		} else {
			return 0;
		}
	}

	/**
	 * Adds an entry to the {@link} pfand with the pfandPrice of the product and the amount.
	 *
	 * @param price  Searches the map {@link} pfand.
	 *               If it finds a key equal to the price, the amount is added to the existing value.
	 * @param amount Gets added to the key, if a key is equal to price.
	 *               If no key equal to price is found, a new entry is added.
	 * @return The new value of the entry is returned.
	 */
	public Long addOrUpdatePfand(BigDecimal price, Long amount) {

		Assert.notNull(price, "InventoryProduct must not be null!");
		Assert.notNull(amount, "Quantity must not be null!");

		Map<BigDecimal, Long> pfandCopy = new LinkedHashMap<>(pfand);

		for (Map.Entry<BigDecimal, Long> e : pfandCopy.entrySet()) {
			if (e.getKey().equals(price)) {
				pfand.put(e.getKey(), pfand.get(e.getKey()) + amount);

				return pfand.get(price);
			}
		}
		pfand.put(price, amount);

		return pfand.get(price);
	}

	/**
	 * Get the sum of the pfand map.
	 *
	 * @return the sum.
	 */
	public BigDecimal getPfandMoney() {

		BigDecimal money = BigDecimal.valueOf(0);

		for (Map.Entry<BigDecimal, Long> e : pfand.entrySet()) {
			money = money.add(e.getKey().multiply(BigDecimal.valueOf(e.getValue())));
		}

		money = money.setScale(SCALE);
		money = money.negate();

		return money;
	}

	public BigDecimal getMwstMoney() {

		BigDecimal money = BigDecimal.valueOf(0);

		for (Map.Entry<InventoryProduct, CartCartItem> e : items.entrySet()) {
			money = money.add(BigDecimal.valueOf(e.getKey().getMwStCategory().getPercentage()).multiply(e.getKey().getPrice()));
		}

		money = money.setScale(SCALE, RoundingMode.HALF_EVEN);

		return money;
	}

	@Override
	public Long getId() {
		return (long) this.hashCode();
	}

	@Override
	public Iterator iterator() {
		return items.values().iterator();
	}
}
