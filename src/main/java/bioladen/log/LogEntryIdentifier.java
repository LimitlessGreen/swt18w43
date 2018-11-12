package bioladen.log;

import org.salespointframework.core.SalespointIdentifier;

import javax.persistence.Embeddable;
import java.util.Map;

/**
 * {@link bioladen.log.LogEntryIdentifier} serves as an identifier type for {@link LogEntry} objects. The main reason for its
 * existence is type safety for identifier across the Salespoint Framework. <br />
 * {@link bioladen.log.LogEntryIdentifier} instances serve as primary key attribute in {@link LogEntry}, but can also be used as a
 * key for non-persistent, {@link Map}-based implementations.
 *
 * @author Jairus Behrisch
 */
@Embeddable
final class LogEntryIdentifier extends SalespointIdentifier {

	private static final long serialVersionUID = -154006278957682012L;

	/**
	 * Creates a new unique identifier for {@link LogEntry}s
	 */
	LogEntryIdentifier() {
		super();
	}

	/**
	 * Only needed for property editor, shouldn't be used otherwise.
	 *
	 * @param logEntryIdentifier The string representation of the identifier.
	 */
	LogEntryIdentifier(String logEntryIdentifier) {
		super(logEntryIdentifier);
	}
}