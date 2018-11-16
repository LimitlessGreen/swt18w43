package bioladen.log;

import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import org.salespointframework.core.AbstractEntity;
import org.salespointframework.time.BusinessTime;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Objects;


@Transactional
@Service
public class Logger implements ApplicationEventPublisherAware
{
	private final LogRepository logRepository;
	private final BusinessTime businessTime;
	private final EntityManager entityManager;

	// external slf4j Logger
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	public Logger(LogRepository logRepository, BusinessTime businessTime, EntityManager entityManager) {
		this.logRepository = logRepository;
		this.businessTime = businessTime;
		this.entityManager = entityManager;
	}

	private LogEntry log(LogLevel logLevel, EntryContainer entryContainer, String message) {
		//get classname of caller, needs to go 3 steps back in StackTrace
		String thrownBy = new Exception().getStackTrace()[3].getClassName();

		LogEntry logEntry = new LogEntry(logLevel, thrownBy, message);

		if (!logEntry.hasSaveTime()) {
			logEntry.setSaveTime(businessTime.getTime());
		}

		if (entryContainer != null) {
			logEntry.addEntryContainer(entryContainer);
			entityManager.persist(logEntry);
		}

		logRepository.save(logEntry);
		publishEvent(logEntry, message);

		//log to console

		switch (logLevel) {
			case INFO: logger.info(message); break;
			case WARNING: logger.warn(message); break;
			case ERROR: logger.error(message); break;
			case DATA:
				if (Objects.requireNonNull(entryContainer).getEntryLevel() != null) {
					logger.info(String.format("Entity %s: %s", entryContainer.getEntryLevel(), message));
				}
				 break;
			default: throw new IllegalArgumentException(logLevel.toString());
		}

		return logEntry;
	}

	private LogEntry log(LogLevel logLevel, String message) {
		return log(logLevel, null, message);
	}

	public LogEntry info(String message) {
		return this.log(LogLevel.INFO, message);
	}

	public LogEntry warning(String message) {
		return this.log(LogLevel.WARNING, message);
	}

	public LogEntry error(String message) {
		return this.log(LogLevel.ERROR, message);
	}

	public <T extends AbstractEntity> LogEntry entity(T entity, EntityLevel entityLevel, String message) {
		EntryContainer entryContainer = new EntryContainer(entity, entityLevel);

		return this.log(LogLevel.DATA, entryContainer, message);
	}

	public <T extends EntityEvent> LogEntry entity(T entityEvent, String message) {
		EntryContainer entryContainer = new EntryContainer(entityEvent.getEntity(), entityEvent.getEventLevel());

		return this.log(LogLevel.DATA, entryContainer, message);
	}

	/* Event publisher */

	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	private void publishEvent(LogEntry logEntry, String message) {
		publisher.publishEvent(new EntityEvent<>(logEntry, EntityLevel.CREATED, message));
	}
}
