package bioladen.datahistory;

import bioladen.customer.Customer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

@Getter
@NoArgsConstructor
@KeySpace("dataHistory")
public class DataEntry<T extends RawEntry> implements RawEntry, ResolvableTypeProvider {

	@Id
	@Setter
	private Long id;

	private T entity;

	private EntityLevel entityLevel;

	private String thrownBy;
	private String publisherName = "unknown";
	private String message = "No message";
	private LinkedHashMap<String, Object> declaredFields = new LinkedHashMap<>();

	private @Setter T entityBeforeModified = null;
	private @Setter String name = null;
	private @Setter Customer involvedCustomer = null;
	private @Setter LocalDateTime saveTime = null;

	DataEntry(String name, EntityLevel entityLevel, String thrownBy, T entity) {
		this.name = name;
		this.entityLevel = entityLevel;
		this.thrownBy = thrownBy;
		this.entity = entity;

		StackTraceElement[] trace = new Exception().getStackTrace();

		// search for the publisher name
		for (StackTraceElement aTrace : trace) {
			if (aTrace.getClassName().contains("bioladen")
					&& !aTrace.getClassName().equals(this.getClass().getName())) {

				this.publisherName = aTrace.getClassName();
				break;
			}
		}

		// fill the declared fields to show in frontend
		Field[] fields = entity.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
					declaredFields.put(field.getName(),
							entity.getClass()
									.getMethod("get" + field.getName().substring(0, 1).toUpperCase()
													+ field.getName().substring(1)).invoke(entity));
				}
				catch (NoSuchMethodException e) { }
				catch (IllegalAccessException e) { }
				catch (InvocationTargetException e) { }
				catch (NullPointerException e) { }
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

	//TODO: pls implement!
	@Override
	public LinkedHashMap<String, DataHistoryRequest> defineCharts() {
		return null;
	}

	@Override
	public Double sumUp(String chartName, Double currentValue) {
		return null;
	}
}
