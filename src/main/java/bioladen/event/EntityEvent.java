package bioladen.event;

import lombok.Getter;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

public class EntityEvent<T> implements ResolvableTypeProvider {


	private @Getter T entity;
	private @Getter EntityLevel eventLevel;
	private @Getter String name;
	private @Getter String message = "No message";
	private @Getter String publisherName = "unknown";
	private @Getter UserAccount involvedUser = null;

	public EntityEvent(String name, T entity, EntityLevel eventLevel) {
		this(name, entity, eventLevel, null, null);
	}

	public EntityEvent(String name, T entity, EntityLevel eventLevel, String message) {
		this(name, entity, eventLevel, null, message);
	}

	public EntityEvent(String name, T entity, EntityLevel eventLevel, UserAccount user) {
		this(name, entity, eventLevel, user, null);
	}

	public EntityEvent(String name, T entity, EntityLevel eventLevel, UserAccount user, String message) {
		this.entity = entity;
		this.eventLevel = eventLevel;
		this.message = (message != null) ? message : this.message;
		this.involvedUser = (user != null) ? user : this.involvedUser;
		this.name = (name != null) ? name : this.entity.getClass().getSimpleName();

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

	@Override
	public String toString() {
		return entity.toString();
	}

}
