package bioladen.event;

import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

public class EntityEvent<T> implements ResolvableTypeProvider {

	private T entity;
	private EventLevel eventLevel;
	private String message = "No message";

	public enum EventLevel {
		CREATED, MODIFIED, DELETED
	}

	public EntityEvent(T entity, EventLevel eventLevel) {
		this.entity = entity;
		this.eventLevel = eventLevel;
	}

	public EntityEvent(T entity, EventLevel eventLevel, String message) {
		this.entity = entity;
		this.eventLevel = eventLevel;
		this.message = message;
	}

	public T getEntity() {
		return entity;
	}

	public String getPublisherName() {
		return entity.getClass().toString();
	}

	public EventLevel getEventLevel() {
		return eventLevel;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics(getClass(),
				ResolvableType.forInstance(entity));
	}
}
