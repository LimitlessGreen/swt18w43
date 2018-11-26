package bioladen.datahistory;

import bioladen.event.EntityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EntityEventListener {

	private final DataHistoryManager dataHistoryManager;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	EntityEventListener(DataHistoryManager dataHistoryManager) {
		this.dataHistoryManager = dataHistoryManager;
	}

	@Async
	@EventListener
	public void listenCustomerEvent(EntityEvent event) {
		// break if is an DataEntry, to prevent event loop
		if (event.getEntity() instanceof DataEntry) {
			return;
		}

		dataHistoryManager.push(event);
		logger.info(String.format("History received entity %s [%s]: {%s}",
				event.getEntity().getClass().getName(), event.getEventLevel(), event.getMessage()));
	}
}
