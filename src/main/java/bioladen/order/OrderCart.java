package bioladen.order;

import bioladen.product.distributor_product.DistributorProduct;
import org.javamoney.moneta.Money;
import org.salespointframework.core.Currencies;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

import javax.money.MonetaryAmount;
import java.util.*;

/**
 * Class to store items before they get ordered
 */
public class OrderCart implements Streamable<OrderCartItem> {

	private final Map<DistributorProduct, OrderCartItem> items = new HashMap<>();


	/**
	 * Calculates the total price
	 * @return the total price
	 */
	public MonetaryAmount getPrice() {

		return items.values().stream() //
				.map(OrderCartItem::getPrice) //
				.reduce(MonetaryAmount::add) //
				.orElse(Money.of(0, Currencies.EURO));
	}

	/**
	 * Adds or updates the count of a item in the Cart
	 * @param product {@link DistributorProduct} to be added or updated
	 * @param quantity Amount
	 * @return the new {@link OrderCartItem}
	 */
	public OrderCartItem addOrUpdateItem(DistributorProduct product, Quantity quantity) {

		Assert.notNull(product, "InventoryProduct must not be null!");
		Assert.notNull(quantity, "Quantity must not be null!");


		for (DistributorProduct distributorProduct : new HashSet<>(items.keySet())) {
			if (distributorProduct.getId() == product.getId()) {
				return items.put(distributorProduct, new OrderCartItem(
															distributorProduct,
															quantity.add(items.get(distributorProduct).getQuantity())));
			}
		}


		return items.put(product, new OrderCartItem(product, quantity));

	}


	/**
	 * Same as {@link #addOrUpdateItem(DistributorProduct, Quantity)} but with a long as amount
	 * @param product
	 * @param amount
	 * @return
	 */
	public OrderCartItem addOrUpdateItem(DistributorProduct product, long amount) {
		return addOrUpdateItem(product, Quantity.of(amount));
	}


	/**
	 * Same as {@link #addOrUpdateItem(DistributorProduct, Quantity)} but with a double as amount
	 * @param product
	 * @param amount
	 * @return
	 */
	public OrderCartItem addOrUpdateItem(DistributorProduct product, double amount) {
		return addOrUpdateItem(product, Quantity.of(amount));
	}

	/**
	 * Removes a item from the card based on the id
	 * @param identifier random generated id of the item
	 * @return {@link Optional<OrderCartItem>} containing the item if it was removed/found
	 */
	public Optional<OrderCartItem> removeItem(String identifier) {

		Assert.notNull(identifier, "CartItem identifier must not be null!");

		return getItem(identifier) //
				.map(item -> items.remove(item.getProduct()));
	}

	/**
	 * Gets a item based on the id
	 * @param identifier random generated id of the item
	 * @return {@link Optional<OrderCartItem>} containing the item if it was found
	 */
	public Optional<OrderCartItem> getItem(String identifier) {

		Assert.notNull(identifier, "OrderCartItem identifier must not be null!");


		return items.values().stream() //
				.filter(item -> item.getId().equals(identifier)) //
				.findFirst();
	}


	/**
	 * Iterator for all cart items
	 * @return Iterator containing all items
	 */
	@Override
	public Iterator iterator() {
		return items.values().iterator();
	}

	/**
	 * Removes all items from the cart
	 */
	public void clear() {
		items.clear();

	}
}
