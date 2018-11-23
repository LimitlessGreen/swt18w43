package bioladen.product;

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

	public InventoryProduct(String name, DistributorProductCatalog distributorProductCatalog) {
		final double PROFIT_MARGIN = 0.20;

		this.name = name;

		this.distributorProducts = distributorProductCatalog.findAll().stream().filter(dp -> dp.getName().equals(name)).collect(Collectors.toList());

		this.price = this.distributorProducts.get(0).getPrice().multiply(BigDecimal.valueOf(1.0 + PROFIT_MARGIN)); //TODO: profit margin onto WHAT price?
		this.unit = this.distributorProducts.get(0).getUnit();

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
	 * Generates a continuous EAN-13 compatible identifier.
	 *
	 * @return the id
	 */
	public String generateId() {
		String maxId = "2000000000008"; //TODO: getMaxId() //// this.qProductCatalog.getMaxId();

		String id;
		if (maxId != null) {
			id = Long.toString(Long.valueOf(maxId.substring(0, 11)) + 1);
			id += getCheckSum(id);
		} else {
			id = "2000000000008"; // minimum possible id including checksum
		}

		return id;
	}

	/**
	 * Generates the checksum of a EAN-13 id.
	 *
	 * @param  id the id
	 * @return    the checksum of the given id
	 */
	public String getCheckSum(String id) {
		int checkSum = 0;
		for (int i = 0; i < id.length(); i++) {
			if (i % 2 == 0) {
				checkSum += (int) id.charAt(i);
			} else {
				checkSum += 3 * (int) id.charAt(i);
			}
		}

		checkSum = 10 - (checkSum % 10);

		return Integer.toString(checkSum);
	}
}
