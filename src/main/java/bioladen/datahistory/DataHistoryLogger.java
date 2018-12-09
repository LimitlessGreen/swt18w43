package bioladen.datahistory;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataHistoryLogger {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Async
	@EventListener
	public void listenEvent(DataEntry event) {
		logger.info(String.format("Event published with %s [%s]: {%s}",
				event.getEntity().getClass().getName(), event.getEntityLevel(), event.toString()));
	}
}
