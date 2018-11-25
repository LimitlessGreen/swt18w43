package bioladen.order;

import bioladen.product.distributor_product.DistributorProduct;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.Currencies;
import org.salespointframework.order.CartItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

import javax.money.MonetaryAmount;
import java.util.*;

public class OrderCart implements Streamable<OrderCartItem> {

	private final Map<DistributorProduct, OrderCartItem> items = new HashMap<>();


	public MonetaryAmount getPrice() {

		return items.values().stream() //
				.map(OrderCartItem::getPrice) //
				.reduce(MonetaryAmount::add) //
				.orElse(Money.of(0, Currencies.EURO));
	}

	public OrderCartItem addOrUpdateItem(DistributorProduct product, Quantity quantity) {

		Assert.notNull(product, "InventoryProduct must not be null!");
		Assert.notNull(quantity, "Quantity must not be null!");


		for (DistributorProduct distributorProduct : new HashSet<>(items.keySet())) {
			if (distributorProduct.getDistributorProductIdentifier() == product.getDistributorProductIdentifier()) {
				return items.put(distributorProduct, new OrderCartItem(distributorProduct, quantity.add(items.get(distributorProduct).getQuantity())));
			}
		}


		return items.put(product, new OrderCartItem(product, quantity));

	}


	public OrderCartItem addOrUpdateItem(DistributorProduct product, long amount) {
		return addOrUpdateItem(product, Quantity.of(amount));
	}


	public OrderCartItem addOrUpdateItem(DistributorProduct product, double amount) {
		return addOrUpdateItem(product, Quantity.of(amount));
	}

	public Optional<OrderCartItem> removeItem(String identifier) {

		Assert.notNull(identifier, "CartItem identifier must not be null!");

		return getItem(identifier) //
				.map(item -> items.remove(item.getProduct()));
	}

	public Optional<OrderCartItem> getItem(String identifier) {

		Assert.notNull(identifier, "OrderCartItem identifier must not be null!");


		return items.values().stream() //
				.filter(item -> item.getId().equals(identifier)) //
				.findFirst();
	}


	@Override
	public Iterator iterator() {
		return items.values().iterator();
	}

	public void clear() {
		items.clear();

	}
}
