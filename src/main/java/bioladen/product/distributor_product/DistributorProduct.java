/**
 * 
 */
package bioladen.product.distributor_product;

import bioladen.product.distributor.Distributor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A offered product of a distributor.
 *
 * @author Adrian Kulisch
 */

@NoArgsConstructor
public class DistributorProduct {

	@Id
	private @Getter String distributorProductIdentifier;

	private @NonNull @Getter @Setter String      name;
	private @NonNull @Getter @Setter Distributor distributor;
	private @NonNull @Getter @Setter BigDecimal  price;
	private @NonNull @Getter @Setter BigDecimal  unit;
	private          @Getter @Setter long        minimumOrderAmount;

	public DistributorProduct(String name, Distributor distributor, BigDecimal price, BigDecimal unit, long minimumOrderAmount) {
		this.name = name;
		this.distributor = distributor;
		this.price = price;
		this.unit = unit;
		this.minimumOrderAmount = minimumOrderAmount;
	}

}
