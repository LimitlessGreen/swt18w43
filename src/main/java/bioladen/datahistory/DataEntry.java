package bioladen.datahistory;

import bioladen.customer.Customer;
import bioladen.event.EntityLevel;
import lombok.Getter;
import lombok.Setter;
import org.salespointframework.core.AbstractEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;

@KeySpace("dataHistory")
@Getter
public class DataEntry<T> {

	@Id //
	private String dataEntryIdentifier;

	// (｡◕‿◕｡)
	// primitve Typen oder Strings müssen nicht extra für JPA annotiert werden
	private EntityLevel entityLevel;
	private String thrownBy;
	private @Setter Customer involvedCustomer = null;
	private @Setter LocalDateTime saveTime = null;
	private @Setter String message = "No message";
	private @Setter String name = null;
	private T entity;
	private @Setter T entityBeforeModified = null;

	public DataEntry() {}

	DataEntry(String name, EntityLevel entityLevel, String thrownBy, T entity) {
		this.name = name;
		this.entityLevel = entityLevel;
		this.thrownBy = thrownBy;
		this.entity = entity;
	}

	/**
	 * Returns a formatted String of the saveTime.
	 *
	 * @param format as defined by {@link java.time.format.DateTimeFormatter}
	 * @return formatted saveTime String
	 */
	public String getFormattedSaveTime(String format) {
		return this.saveTime.format(DateTimeFormatter.ofPattern(format));
	}

	public Optional<T> getEntityBeforeModified() {
		return Optional.ofNullable(entityBeforeModified);
	}

	/**
	 * Returns an user readable String of DataEntry
	 *
	 * @return
	 */
	@Override
	public String toString(){
		return String.format("from %s push [%s]. Message: %s",
				this.thrownBy,
				this.entityLevel,
				this.message);
	}
}
