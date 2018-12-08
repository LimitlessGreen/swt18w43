package bioladen.datahistory;

import bioladen.customer.CustomerTools;
import lombok.RequiredArgsConstructor;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.time.Interval;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class DataHistoryManager<T extends RawEntry> implements ApplicationEventPublisherAware {

	private final CustomerTools customerTools;
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
			UserAccount involvedUser) {

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

		DataEntry dataEntry = new DataEntry<>(name, entityLevel, thrownBy, entity);

		if (entityLevel.equals(EntityLevel.MODIFIED)) {
			dataEntry.setEntityBeforeModified(
					findLatestCreatedOrModified(entity)
			);
		}

		dataEntry.setMessage(message);
		dataEntry.setSaveTime(businessTime.getTime());
		dataEntry.setInvolvedCustomer(customerTools.userToCustomer(involvedUser).orElse(null));

		//TODO: auto generate incremented id!
		dataEntry.setId(dataEntryRepository.findAllByOrderById().size() + 1L);

		dataEntryRepository.save(dataEntry);

		// (👁 ᴥ 👁) Event
		publishEvent(dataEntry);

		return dataEntry;
	}

	// normal push in some variations
	public DataEntry push(String name,
							  T entity,
							  EntityLevel entityLevel,
							  String message,
							  UserAccount involvedUser) {

		return this.log(name, entity, entityLevel, message, null, involvedUser);
	}
	public DataEntry push(String name, T entity, EntityLevel entityLevel, String message) {

		return this.push(name, entity, entityLevel, message, null);
	}

	public DataEntry push(String name, T entity, EntityLevel entityLevel, UserAccount involvedUser) {

		return this.push(name, entity, entityLevel, null, involvedUser);
	}
	public DataEntry push(String name, T entity, EntityLevel entityLevel) {

		return this.push(name, entity, entityLevel, null, null);
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

	public DataEntry findLatestCreatedOrModified(T entity) {

		Class entityClass = entity.getClass();

		DataEntry output = null;
		for (DataEntry entry : dataEntryRepository.findAllByOrderById()) {

			if (entry.getEntity().getClass().equals(entityClass)
					&& entry.getEntityLevel() != EntityLevel.DELETED
					&& entry.getEntity().equals(entity)) {
				output = entry;
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

	private void publishEvent(DataEntry dataEntry) {
		publisher.publishEvent(dataEntry);
	}
}
