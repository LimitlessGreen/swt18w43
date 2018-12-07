package bioladen.event;

import bioladen.datahistory.RawEntry;
import lombok.Getter;
import lombok.Setter;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.util.Optional;

@Getter
public class EntityEvent<T extends RawEntry> implements ResolvableTypeProvider {


	private T entity;
	private T entityBeforeModified = null;
	@Setter
	private EntityLevel eventLevel;
	private String name;
	private String message = "No message";
	private String publisherName = "unknown";
	private UserAccount involvedUser = null;

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

	public Optional<T> getEntityBeforeModified() {
		return Optional.ofNullable(entityBeforeModified);
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
