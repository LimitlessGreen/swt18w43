/**
 *
 */
package bioladen.product;

import bioladen.product.distributor_product.DistributorProduct;
import bioladen.product.distributor_product.DistributorProductCatalog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A product.
 *
 * @author Adrian Kulisch
 */

@NoArgsConstructor
public class Product {
	@Id
	private @Getter String productIdentifier;

	private @NonNull @Getter @Setter String      name;
	private @NonNull @Getter @Setter BigDecimal  price;
	private @NonNull @Getter @Setter BigDecimal  unit;
	private          @Getter @Setter long        inventoryAmount;
	private          @Getter @Setter long        displayedAmount;

	private @Getter @Setter List<DistributorProduct> distributorProducts;

	public Product(String name, DistributorProductCatalog distributorProductCatalog) {
		final double PROFIT_MARGIN = 0.20;

		this.name = name;

		this.distributorProducts = distributorProductCatalog.findAll().stream().filter(dp -> dp.getName().equals(name)).collect(Collectors.toList());

		this.price = this.distributorProducts.get(0).getPrice().multiply(BigDecimal.valueOf(1.0 + PROFIT_MARGIN)); //TODO: profit margin onto WHAT price?
		this.unit = this.distributorProducts.get(0).getUnit();

		this.inventoryAmount = 0;
		this.displayedAmount = 0;
	}

	/**
	 * Generates a continuous EAN-13 compatible identifier.
	 *
	 * @return the id
	 */
	public String generateId() {
		String maxId = this.qProductCatalog.getMaxId();

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
