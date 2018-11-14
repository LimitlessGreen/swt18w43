package bioladen.log;

import org.salespointframework.core.SalespointIdentifier;

import javax.persistence.Embeddable;
import java.util.Map;

/**
 * {@link bioladen.log.EntryContainerIdentifier} serves as an identifier type for {@link EntryContainer} objects. The main reason for its
 * existence is type safety for identifier across the Salespoint Framework. <br />
 * {@link bioladen.log.EntryContainerIdentifier} instances serve as primary key attribute in {@link EntryContainer}, but can also be used as a
 * key for non-persistent, {@link Map}-based implementations.
 *
 * @author Jairus Behrisch
 */
@Embeddable
final class EntryContainerIdentifier extends SalespointIdentifier {

	private static final long serialVersionUID = 154706278957632012L;

	/**
	 * Creates a new unique identifier for {@link EntryContainer}s
	 */
	EntryContainerIdentifier() {
		super();
	}

	/**
	 * Only needed for property editor, shouldn't be used otherwise.
	 *
	 * @param entryContainerIdentifier The string representation of the identifier.
	 */
	EntryContainerIdentifier(String entryContainerIdentifier) {
		super(entryContainerIdentifier);
	}
}