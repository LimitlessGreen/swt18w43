package bioladen.product;

import javax.persistence.Embeddable;
import java.util.Map;

import org.salespointframework.core.SalespointIdentifier;

/**
 * {@link bioladen.product.DistributorProductIdentifier} serves as an identifier type for {@link DistributorProduct} objects. The main reason for its
 * existence is type safety for identifier across the Salespoint Framework. <br />
 * {@link bioladen.product.DistributorProductIdentifier} instances serve as primary key attribute in {@link DistributorProduct}, but can also be used as a
 * key for non-persistent, {@link Map}-based implementations.
 *
 * @author Adrian Kulisch
 */
@Embeddable
final class DistributorProductIdentifier extends SalespointIdentifier {

	private static final long serialVersionUID = -5608924622751638769L;

	/**
	 * Creates a new unique identifier for {@link DistributorProduct}s
	 */
	DistributorProductIdentifier() {
		super();
	}

	/**
	 * Only needed for property editor, shouldn't be used otherwise.
	 *
	 * @param distributorProductIdentifier The string representation of the identifier.
	 */
	DistributorProductIdentifier(String distributorProductIdentifier) {
		super(distributorProductIdentifier);
	}
}
