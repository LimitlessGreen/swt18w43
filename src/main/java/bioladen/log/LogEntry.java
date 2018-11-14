package bioladen.log;

import lombok.Getter;
import lombok.Setter;
import org.salespointframework.core.AbstractEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "LOG")
public class LogEntry<T> extends AbstractEntity<LogEntryIdentifier> {

	@EmbeddedId //
	@AttributeOverride(name = "id", column = @Column(name = "LOG_ID")) //
	private LogEntryIdentifier logEntryIdentifier = new LogEntryIdentifier();

	// (｡◕‿◕｡)
	// primitve Typen oder Strings müssen nicht extra für JPA annotiert werden
	private @Getter LogLevel logLevel;
	private @Getter String thrownBy;
	private @Setter LocalDateTime saveTime = null;
	private @Getter String message = "No message";
	private @Setter @Getter EntryContainer entry = null;

	public LogEntry(LogLevel logLevel, String thrownBy, String message) {
		this.logLevel = logLevel;
		this.thrownBy = thrownBy;
		this.message = message;
	}

	/**
	 * Returns whether the {@link LogEntry} already has a {@link this.saveTime} set.
	 *
	 * @return
	 */
	public boolean hasSaveTime() {
		return saveTime != null;
	}

	/**
	 * @return the {@link LocalDateTime} when this entry was posted.
	 */
	public final Optional<LocalDateTime> getSaveTime() {
		return Optional.ofNullable(saveTime);
	}

	/**
	 * Returns whether the {@link EntryContainer} already has a {@link this.entry} set.
	 *
	 * @return
	 */
	public boolean hasEntry(){
		return entry != null;
	}

	@Override
	public LogEntryIdentifier getId() {
		return logEntryIdentifier;
	}
}
