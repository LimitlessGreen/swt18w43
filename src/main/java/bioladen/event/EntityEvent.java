package bioladen.event;

import lombok.Getter;
import org.salespointframework.core.AbstractEntity;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

public class EntityEvent<T extends AbstractEntity> implements ResolvableTypeProvider {

	private @Getter T entity;
	private @Getter EntityLevel eventLevel;
	private @Getter String message = "No message";

	public EntityEvent(T entity, EntityLevel eventLevel) {
		this.entity = entity;
		this.eventLevel = eventLevel;
	}

	public EntityEvent(T entity, EntityLevel eventLevel, String message) {
		this.entity = entity;
		this.eventLevel = eventLevel;
		this.message = message;
	}

	public String getPublisherName() {
		return entity.getClass().toString();
	}

	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics(getClass(),
				ResolvableType.forInstance(entity));
	}
}
