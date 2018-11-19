package bioladen.product.distributor_product;

import bioladen.product.distributor.Distributor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

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
