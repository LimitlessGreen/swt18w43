package bioladen.log;

import lombok.Getter;
import lombok.Setter;
import org.salespointframework.core.AbstractEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "LOG")
public class LogEntry extends AbstractEntity<LogEntryIdentifier> {

	@EmbeddedId //
	@AttributeOverride(name = "id", column = @Column(name = "LOG_ID")) //
	private LogEntryIdentifier logEntryIdentifier = new LogEntryIdentifier();

	// (｡◕‿◕｡)
	// primitve Typen oder Strings müssen nicht extra für JPA annotiert werden
	private @Getter LogLevel logLevel;
	private @Getter String thrownBy;
	private @Setter LocalDateTime saveTime = null;
	private @Getter String message = "No message";

	@OneToMany(mappedBy = "logEntry", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EntryContainer> entryContainers = new ArrayList<>();

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

	public void addEntryContainer(EntryContainer entryContainer) {
		entryContainers.add(entryContainer);
		entryContainer.setLogEntry(this);
	}

	/**
	 * Returns an user readable String of LogEntry
	 *
	 * @return
	 */
	@Override
	public String toString(){
		return String.format("from %s entity [%s]. Message: %s",
				this.thrownBy,
				this.logLevel,
				this.message);
	}

	@Override
	public LogEntryIdentifier getId() {
		return logEntryIdentifier;
	}
}
