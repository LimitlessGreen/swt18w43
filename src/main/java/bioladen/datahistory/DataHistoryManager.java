package bioladen.datahistory;

import bioladen.customer.Customer;
import bioladen.customer.CustomerManager;
import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import lombok.RequiredArgsConstructor;
import org.salespointframework.time.BusinessTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataHistoryManager implements ApplicationEventPublisherAware {

	private final CustomerManager customerManager;
	private final DataEntryRepository dataEntryRepository;
	private final BusinessTime businessTime;

	private<T> DataEntry log(
			EntityLevel entityLevel,
			T entity, String message,
			String publisherName,
			Customer involvedCustomer) {

		//get classname of caller, needs to search back in StackTrace

		String thrownBy = "Unknown";
		if (publisherName != null) {
			thrownBy = publisherName;
		} else {
			StackTraceElement[] trace = new Exception().getStackTrace();

			for (StackTraceElement aTrace : trace) {
				if (aTrace.getClassName().contains("bioladen")
						&& !aTrace.getClassName().equals(this.getClass().getName())) {

					thrownBy = aTrace.getClassName();
					break;
				}
			}
		}

		DataEntry dataEntry = new DataEntry(entityLevel, thrownBy, entity);

		dataEntry.setSaveTime(businessTime.getTime());
		dataEntry.setInvolvedCustomer(involvedCustomer);

		dataEntryRepository.save(dataEntry);
		publishEvent(dataEntry, message);

		return dataEntry;
	}

	// normal push in some variations
	public <T> DataEntry push(T entity, EntityLevel entityLevel, String message, Customer involvedCustomer) {
		if (message == null) {
			message = entity.toString();
		}

		return this.log(entityLevel, entity, message, null, involvedCustomer);
	}
	public <T> DataEntry push(T entity, EntityLevel entityLevel, String message) {

		return this.push(entity, entityLevel, message, null);
	}

	public <T> DataEntry push(T entity, EntityLevel entityLevel, Customer involvedCustomer) {

		return this.push(entity, entityLevel, null, involvedCustomer);
	}

	public <T> DataEntry push(T entity, EntityLevel entityLevel) {

		return this.push(entity, entityLevel, null, null);
	}

	// only for events
	public <T extends EntityEvent> DataEntry push(T entityEvent) {

		return this.log(
				entityEvent.getEventLevel(),
				entityEvent.getEntity(),
				entityEvent.getMessage(),
				entityEvent.getPublisherName(),
				customerManager.userToCustomer(entityEvent.getInvolvedUser()).orElse(null));
	}

	/* Event publisher */

	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	private void publishEvent(DataEntry dataEntry, String message) {
		publisher.publishEvent(new EntityEvent<>(dataEntry, EntityLevel.CREATED, message));
	}
}
