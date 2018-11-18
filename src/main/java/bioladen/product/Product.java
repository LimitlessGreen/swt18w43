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
import javax.persistence.*;
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

	private final double PROFIT_MARGIN = 0.20;

	@Id
	private @Getter String productIdentifier;

	private @NonNull @Getter @Setter String      name;
	private @NonNull @Getter @Setter BigDecimal  price;
	private @NonNull @Getter @Setter BigDecimal  unit;
	private          @Getter @Setter long        inventoryAmount;
	private          @Getter @Setter long        displayedAmount;

	private @Getter @Setter List<DistributorProduct> distributorProducts;

	public Product(String name, DistributorProductCatalog distributorProductCatalog) {
		this.name = name;

		this.distributorProducts = distributorProductCatalog.findAll().stream().filter(dp -> dp.getName().equals(name)).collect(Collectors.toList());

		this.price = this.distributorProducts.get(0).getPrice().multiply(BigDecimal.valueOf(1.0 + PROFIT_MARGIN)); //TODO: profit margin onto WHAT price?
		this.unit = this.distributorProducts.get(0).getUnit();

		this.inventoryAmount = 0;
		this.displayedAmount = 0;
	}
}
