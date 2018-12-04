package bioladen.datahistory;

import bioladen.event.EntityEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntityEventListener {

	private final DataHistoryManager dataHistoryManager;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Async
	@EventListener
	public void listenEvent(EntityEvent event) {
		// break if is an DataEntry, to prevent event loop
		if (event.getEntity() instanceof DataEntry) {
			return;
		}

		dataHistoryManager.push(event);
		logger.info(String.format("History received entity %s [%s]: {%s}",
				event.getEntity().getClass().getName(), event.getEventLevel(), event.toString()));
	}
}
