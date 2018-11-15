package bioladen.log;

import bioladen.customer.Customer;
import bioladen.event.EntityEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CustomerEventListener {

	private final LogRepository logRepository;
	private final Logger logger;

	CustomerEventListener(LogRepository logRepository, Logger logger) {
		this.logRepository = logRepository;
		this.logger = logger;
	}

	@Async
	@EventListener
	public void listenCustomerEvent(EntityEvent<Customer> event) {
		logger.entity(event, event.getEntity().toString());
	}
}
