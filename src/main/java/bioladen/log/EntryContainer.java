package bioladen.log;

import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import lombok.Getter;
import lombok.Setter;
import org.salespointframework.core.AbstractEntity;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "ENTRY_CONTAINER")
public class EntryContainer extends AbstractEntity {
	@EmbeddedId //
	@AttributeOverride(name = "id", column = @Column(name = "CONTAINER_ID")) //
	private EntryContainerIdentifier entryContainerIdentifier = new EntryContainerIdentifier();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOG_ID")
	private @Getter @Setter LogEntry logEntry;

	private @Getter EntityLevel entryLevel;

	//private Order order = null;
	// TODO: add some other entities

	public<T extends EntityEvent> EntryContainer(T entity) {
		constructorHelper(entity.getEntity(), entity.getEventLevel());
	}

	public<T extends AbstractEntity> EntryContainer(T entity, EntityLevel entryLevel) {
		constructorHelper(entity, entryLevel);
	}

	private<T extends AbstractEntity> void constructorHelper(T entity, EntityLevel entryLevel) {

		this.entryLevel = entryLevel;

		if (entity instanceof LogEntry) {
			this.logEntry = (LogEntry) entity;
		}
		//if (entity instanceof Order) {
		//	this.order = (Order) entity;
		//}
	}

	public final Optional<LogEntry> getLogEntry() {
		return Optional.ofNullable(logEntry);
	}

	//public final Optional<Order> getOrder() {
	//	return Optional.ofNullable(order);
	//}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EntryContainer)) return false;
		return entryContainerIdentifier != null && entryContainerIdentifier.equals(((EntryContainer) o).getId());
	}

	@Override
	public EntryContainerIdentifier getId() {
		return entryContainerIdentifier;
	}
}
