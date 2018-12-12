package bioladen.product.distributor_product;

import bioladen.datahistory.RawEntry;
import bioladen.product.MwStCategory;
import bioladen.product.Organization;
import bioladen.product.ProductCategory;
import bioladen.product.distributor.Distributor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * A offered product of a distributor.
 *
 * @author Adrian Kulisch
 */

@Entity
@Table(name = "DISTRIBUTOR_PRODUCT")
@NoArgsConstructor
public class DistributorProduct implements RawEntry, Comparable<DistributorProduct> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private @Getter
	Long id;

	private @NonNull
	@Getter
	@Setter
	String name;
	private @NonNull
	@Getter
	@Setter
	BigDecimal price;
	private @NonNull
	@Getter
	@Setter
	BigDecimal unit;
	private @Getter
	@Setter
	long minimumOrderAmount;
	private @NonNull
	@Getter
	@Setter
	ProductCategory productCategory;
	private @NonNull
	@Getter
	@Setter
	MwStCategory mwStCategory;
	private @Getter
	@Setter
	BigDecimal pfandPrice;
	private @Getter
	@Setter
	Organization organization;

	@OneToOne
	private @NonNull
	@Getter
	@Setter
	Distributor distributor;

	public DistributorProduct(
			String name,
			Distributor distributor,
			BigDecimal price,
			BigDecimal unit,
			long minimumOrderAmount,
			ProductCategory productCategory,
			MwStCategory mwStCategory,
			BigDecimal pfandPrice,
			Organization organization) {
		this.name = name;
		this.distributor = distributor;
		this.price = price;
		this.unit = unit;
		this.minimumOrderAmount = minimumOrderAmount;
		this.productCategory = productCategory;
		this.mwStCategory = mwStCategory;
		this.pfandPrice = pfandPrice;
		this.organization = organization;
	}

	@Override
	public String toString() {
		return String.format(
				"%s: {distributor: %s, price: %s, unit: %s, minimumOrderAmount: %s}",
				name, distributor.getName(), price, unit, minimumOrderAmount);
	}

	@Override
	public int compareTo(DistributorProduct distributorProduct) {
		//a negative integer, zero, or a positive integer as this object
		//is less than, equal to, or greater than the specified object.
		final int nameCompare = getName().compareTo(distributorProduct.getName());
		final int priceCompare = getPrice().compareTo(distributorProduct.getPrice());

		if (nameCompare == 0 && priceCompare == 0)
			return getId().compareTo(distributorProduct.getId());
		else if (nameCompare == 0)
			return priceCompare;

		return nameCompare;
	}
}
