package bioladen.event;

import org.springframework.context.ApplicationEvent;

public class EntityCreatedEvent extends ApplicationEvent {

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public EntityCreatedEvent(Object source) {
		super(source);
	}

}
