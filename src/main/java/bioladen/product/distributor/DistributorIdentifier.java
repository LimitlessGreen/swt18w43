package bioladen.product.distributor;

import javax.persistence.Embeddable;
import java.util.Map;

import org.salespointframework.core.SalespointIdentifier;

/**
 * {@link bioladen.product.distributor.DistributorIdentifier} serves as an identifier type for {@link Distributor} objects. The main reason for its
 * existence is type safety for identifier across the Salespoint Framework. <br />
 * {@link bioladen.product.distributor.DistributorIdentifier} instances serve as primary key attribute in {@link Distributor}, but can also be used as a
 * key for non-persistent, {@link Map}-based implementations.
 *
 * @author Adrian Kulisch
 */
@Embeddable
final class DistributorIdentifier extends SalespointIdentifier {

	private static final long serialVersionUID = -3812418746101743679L;
	
	/**
	 * Creates a new unique identifier for {@link Distributor}s
	 */
	DistributorIdentifier() {
		super();
	}

	/**
	 * Only needed for property editor, shouldn't be used otherwise.
	 *
	 * @param distributorIdentifier The string representation of the identifier.
	 */
	DistributorIdentifier(String distributorIdentifier) {
		super(distributorIdentifier);
	}
}
