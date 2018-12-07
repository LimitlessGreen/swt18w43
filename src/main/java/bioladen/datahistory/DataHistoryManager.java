package bioladen.datahistory;

import bioladen.customer.Customer;
import bioladen.customer.CustomerManager;
import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import lombok.RequiredArgsConstructor;
import org.salespointframework.core.AbstractEntity;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.time.Interval;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataHistoryManager<T extends RawEntry> implements ApplicationEventPublisherAware {

	private final CustomerManager customerManager;
	private final DataEntryRepository dataEntryRepository;
	private final BusinessTime businessTime;

	/*----------------------*/
	/*  1. Log
	/*----------------------*/

	private DataEntry log(
			String name,
			T entity, EntityLevel entityLevel,
			String message,
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

		DataEntry dataEntry = new DataEntry(name, entityLevel, thrownBy, entity);

		dataEntry.setSaveTime(businessTime.getTime());
		dataEntry.setInvolvedCustomer(involvedCustomer);

		dataEntryRepository.save(dataEntry);

		// (👁 ᴥ 👁) Event
		publishEvent(dataEntry, message);

		return dataEntry;
	}

	// normal push in some variations
	public DataEntry push(String name,
							  T entity,
							  EntityLevel entityLevel,
							  String message,
							  Customer involvedCustomer) {
		if (message == null) {
			message = entity.toString();
		}

		return this.log(name, entity, entityLevel, message, null, involvedCustomer);
	}
	public DataEntry push(String name, T entity, EntityLevel entityLevel, String message) {

		return this.push(name, entity, entityLevel, message, null);
	}

	public DataEntry push(String name, T entity, EntityLevel entityLevel, Customer involvedCustomer) {

		return this.push(name, entity, entityLevel, null, involvedCustomer);
	}
	public DataEntry push(String name, T entity, EntityLevel entityLevel) {

		return this.push(name, entity, entityLevel, null, null);
	}

	// only for events
	public <S extends EntityEvent<T>> DataEntry push(S entityEvent) {

		return this.log(
				entityEvent.getName(),
				entityEvent.getEntity(),
				entityEvent.getEventLevel(),
				entityEvent.getMessage(),
				entityEvent.getPublisherName(),
				customerManager.userToCustomer(entityEvent.getInvolvedUser()).orElse(null));
	}

	/*----------------------*/
	/*  2. FindBys
	/*----------------------*/

	public LinkedList<DataEntry> findBy(Class entityClass, EntityLevel entityLevel, Interval interval) {

		LinkedList<DataEntry> output = new LinkedList<>();

		for (DataEntry entry : dataEntryRepository.findByEntityLevelAndSaveTimeBetweenOrderBySaveTimeDesc
				(entityLevel, interval.getStart(), interval.getEnd())) {

			if (entry.getEntity().getClass().equals(entityClass)) {
				output.add(entry);
			}
		}

		return output;

	}

	/*
         _________________
        < Event publisher >
         -----------------
            \   ^__^
             \  (@@)\_______
                (__)\       )\/\
                    ||----w |
                    ||     ||

    */

	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	private void publishEvent(DataEntry dataEntry, String message) {
		publisher.publishEvent(new EntityEvent<>(null, dataEntry, EntityLevel.CREATED, message));
	}
}
