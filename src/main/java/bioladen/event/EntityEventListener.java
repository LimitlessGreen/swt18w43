package bioladen.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/*
 * This is only an example.
 * Use "void handleEvent(EntityEvent<T> event)"
 * if you want to listen for a specific generic event.
 */

@Component
public class EntityEventListener {

	@Async
	@EventListener
	public void handleEvent(EntityEvent event)
	{
		System.out.println(String.format("%s [%s]: %s",
				event.getResolvableType().getGeneric(),
				event.getEventLevel(),
				event.getMessage()));
	}
}

