package bioladen.event;

import lombok.Getter;
import org.salespointframework.core.AbstractEntity;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

public class EntityEvent<T> implements ResolvableTypeProvider {

	private @Getter T entity;
	private @Getter EntityLevel eventLevel;
	private @Getter String message;
	private @Getter String publisherName = "unknown";

	public EntityEvent(T entity, EntityLevel eventLevel) {
		this(entity, eventLevel, entity.toString());
	}

	public EntityEvent(T entity, EntityLevel eventLevel, String message) {
		this.entity = entity;
		this.eventLevel = eventLevel;
		this.message = message;

		StackTraceElement[] trace = new Exception().getStackTrace();

		for (StackTraceElement aTrace : trace) {
			if (aTrace.getClassName().contains("bioladen")
					&& !aTrace.getClassName().equals(this.getClass().getName())) {

				this.publisherName = aTrace.getClassName();
				break;
			}
		}
	}

	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics(getClass(),
				ResolvableType.forInstance(entity));
	}
}
