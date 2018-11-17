package bioladen.datahistory;

import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import org.salespointframework.time.BusinessTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

@Service
public class DataHistoryManager implements ApplicationEventPublisherAware
{
	private final DataEntryRepository dataEntryRepository;
	private final BusinessTime businessTime;

	public DataHistoryManager(DataEntryRepository dataEntryRepository, BusinessTime businessTime) {
		this.dataEntryRepository = dataEntryRepository;
		this.businessTime = businessTime;
	}

	private<T> DataEntry log(EntityLevel entityLevel, T entity, String message) {
		//get classname of caller, needs to go 3 steps back in StackTrace
		String thrownBy = new Exception().getStackTrace()[3].getClassName();

		DataEntry dataEntry = new DataEntry(entityLevel, thrownBy, entity);

		if (!dataEntry.hasSaveTime()) {
			dataEntry.setSaveTime(businessTime.getTime());
		}

		dataEntryRepository.save(dataEntry);
		publishEvent(dataEntry, message);

		return dataEntry;
	}

	public <T> DataEntry push(T entity, EntityLevel entityLevel, String message) {
		if (message == null) message = entity.toString();

		return this.log(entityLevel, entity, message);
	}
	public <T> DataEntry push(T entity, EntityLevel entityLevel) {

		return this.push(entity, entityLevel, null);
	}

	public <T extends EntityEvent> DataEntry push(T entityEvent) {

		return this.log(entityEvent.getEventLevel(), entityEvent.getEntity(), entityEvent.getMessage());
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
