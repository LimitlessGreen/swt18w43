package bioladen.log;

import lombok.Setter;
import org.salespointframework.core.AbstractEntity;
import org.salespointframework.order.Order;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.Optional;

@Entity
public class EntryContainer extends AbstractEntity {
	@EmbeddedId //
	@AttributeOverride(name = "id", column = @Column(name = "CONTAINER_ID")) //
	private EntryContainerIdentifier entryContainerIdentifier = new EntryContainerIdentifier();

	private @Setter LogEntry logEntry = null;
	private @Setter Order order = null;
	// TODO: add some other entities

	public EntryContainer() {

	}

	public final Optional<LogEntry> getLogEntry() {
		return Optional.ofNullable(logEntry);
	}

	public final Optional<Order> getOrder() {
		return Optional.ofNullable(order);
	}

	@Override
	public EntryContainerIdentifier getId() {
		return entryContainerIdentifier;
	}
}
