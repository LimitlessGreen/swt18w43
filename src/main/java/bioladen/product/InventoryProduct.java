package bioladen.product;

import bioladen.product.distributor.Distributor;
import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A product.
 *
 * @author Adrian Kulisch
 */

@Entity
@Table(name = "INVENTORY_PRODUCT")
@NoArgsConstructor
public class InventoryProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inventoryProductIdentifier", updatable = false, nullable = false)
	private @Getter Long productIdentifier;

	private @NonNull @Getter @Setter String      name;
	private @NonNull @Getter @Setter BigDecimal  price;
	private @NonNull @Getter @Setter BigDecimal  unit;
	private          @Getter @Setter long        inventoryAmount;
	private          @Getter @Setter long        displayedAmount;

	@OneToMany(cascade=CascadeType.ALL)
	private @Getter @Setter List<DistributorProduct> distributorProducts;

	public InventoryProduct(DistributorProduct distributorProduct, DistributorProductCatalog distributorProductCatalog) {
		final double PROFIT_MARGIN = 0.20;

		this.name = distributorProduct.getName();

		this.distributorProducts = distributorProductCatalog.findAll().stream()
				.filter(dp -> dp.getName().equals(this.name)).collect(Collectors.toList());

		this.price = distributorProduct.getPrice().multiply(BigDecimal.valueOf(1.0 + PROFIT_MARGIN));
		this.unit = distributorProduct.getUnit();

		this.inventoryAmount = 0;
		this.displayedAmount = 0;
	}

	/**
	 * Removes amounts from the displayedAmount. Readd units with negative amount.
	 *
	 * @param amount Amount to be removed (added if < 0)
	 * @return true if successful; else false
	 */
	public boolean removeDisplayedAmount(long amount) {
		if (displayedAmount >= amount) {
			displayedAmount -= amount;

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Generates a continuous EAN-13 compatible identifier from a given id.
	 *
	 * @return the ean-13 id
	 */
	public long toEan13(long id) {
		final long ean13Base = 200000000000L;

		long ean13id = (id + ean13Base);
		ean13id = ean13id * 10 + getCheckSum(Long.toString(ean13id));

		return ean13id;
	}

	/**
	 * Generates the original id from an EAN-13 identifier.
	 *
	 * @return the id
	 */
	public long fromEan13(long ean13id) {
		final long ean13Base = 200000000000L;

		return ean13id / 10 - ean13Base;
	}

	/**
	 * Generates the checksum of a EAN-13 id from the 12 digits before.
	 *
	 * @param  id the id (the 12 digits before)
	 * @return    the checksum of the given id
	 */
	private int getCheckSum(String id) {
		int checkSum = 0;
		for (int i = 0; i < id.length(); i++) {
			if (i % 2 == 0) {
				checkSum += (int) id.charAt(i);
			} else {
				checkSum += 3 * (int) id.charAt(i);
			}
		}

		checkSum = 10 - (checkSum % 10);

		return checkSum;
	}

	@Override
	public String toString() {
		return String.format(
				"%s: {price: %s, unit: %s, inventoryAmount: %s, displayedAmount: %s}",
				name, price, unit, inventoryAmount, displayedAmount);
	}
}
