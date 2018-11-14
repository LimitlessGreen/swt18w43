/**
 * 
 */
package bioladen.product;

import java.util.ArrayList;
import java.util.List;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * A product in inventory.
 * 
 * @author Adrian Kulisch
 */

//@Entity
@ToString
public class Product extends org.salespointframework.catalog.Product {
	
	private @NonNull @Getter @Setter Quantity unit;
	
	private long inventoryAmount = 0;
	private long displayedAmount = 0;
	
	@OneToMany
	private List<DistributorProduct> distributorProducts = new ArrayList<>();
	
	private Product() {}
	
	private Product(String name, Quantity unit, Metric unitMetric, MonetaryAmount unitPrice, List<DistributorProduct> distributorProducts) {
		super(name, unitPrice, unitMetric);
		
		this.unit = unit;
		this.distributorProducts = distributorProducts;
	}
	
	public Quantity getUnit() {
		return this.unit;
	}
	
	public long getInventoryAmount() {
		return this.inventoryAmount;
	}
	
	public long getDisplayedAmount() {
		return this.displayedAmount;
	}
	
	public Iterable<DistributorProduct> getDistributorProducts() {
		return this.distributorProducts;
	}
}
