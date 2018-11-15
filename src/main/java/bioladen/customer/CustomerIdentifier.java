package bioladen.customer;

import org.salespointframework.core.SalespointIdentifier;

import javax.persistence.Embeddable;
import java.util.Map;
/**
 * {@link bioladen.customer.CustomerIdentifier} serves as an identifier type for {@link Customer} objects. The main reason for its
 * existence is type safety for identifier across the Salespoint Framework. <br />
 * {@link bioladen.customer.CustomerIdentifier} instances serve as primary key attribute in {@link Customer}, but can also be used as a
 * key for non-persistent, {@link Map}-based implementations.
 *
 * @author Lisa Riedel
 */

@Embeddable
public class CustomerIdentifier extends  SalespointIdentifier{

		private static final long serialVersionUID = 154709278657932012L;

		/**
		 * Creates a new unique identifier for {@link Customer}s
		 */
		CustomerIdentifier() {
			super();
		}

		/**
		 * Only needed for property editor, shouldn't be used otherwise.
		 *
		 * @param customerIdentifier The string representation of the identifier.
		 */
		CustomerIdentifier(String customerIdentifier) {
			super(customerIdentifier);
		}

}

