package bioladen.datahistory;

import bioladen.event.EntityLevel;
import lombok.Getter;
import lombok.Setter;
import org.salespointframework.core.AbstractEntity;

import java.time.LocalDateTime;
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
	private @Setter LocalDateTime saveTime = null;
	private @Setter String message = "No message";
	private T entity;
	// TODO for later usage: add a user, who interacted with the push

	public DataEntry() {}

	public DataEntry(EntityLevel entityLevel, String thrownBy, T entity) {
		this.entityLevel = entityLevel;
		this.thrownBy = thrownBy;
		this.entity = entity;
	}

	/**
	 * Returns whether the {@link DataEntry} already has a {@link this.saveTime} set.
	 *
	 * @return
	 */
	public boolean hasSaveTime() {
		return saveTime != null;
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
