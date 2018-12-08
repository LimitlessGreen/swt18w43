package bioladen.datahistory;

import bioladen.customer.Customer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@KeySpace("dataHistory")
@Getter
public class DataEntry<T extends RawEntry> implements RawEntry, ResolvableTypeProvider {

	@Id
	@Setter
	private Long id;

	// (｡◕‿◕｡)
	// primitve Typen oder Strings müssen nicht extra für JPA annotiert werden
	private         T entity;
	private @Setter T entityBeforeModified = null;
	private         EntityLevel entityLevel;
	private         String thrownBy;
	private @Setter String name = null;
	private         String publisherName = "unknown";
	private @Setter Customer involvedCustomer = null;
	private @Setter LocalDateTime saveTime = null;
	private         String message = "No message";

	public DataEntry() {}

	DataEntry(String name, EntityLevel entityLevel, String thrownBy, T entity) {
		this.name = name;
		this.entityLevel = entityLevel;
		this.thrownBy = thrownBy;
		this.entity = entity;

		StackTraceElement[] trace = new Exception().getStackTrace();

		for (StackTraceElement aTrace : trace) {
			if (aTrace.getClassName().contains("bioladen")
					&& !aTrace.getClassName().equals(this.getClass().getName())) {

				this.publisherName = aTrace.getClassName();
				break;
			}
		}
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

	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics(getClass(),
				ResolvableType.forInstance(entity));
	}

	public void setMessage(String message) {
		this.message = (message != null) ? message : this.message;
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
