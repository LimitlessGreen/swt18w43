package bioladen.log;

import org.springframework.data.repository.CrudRepository;

interface LogRepository<T extends LogEntry> extends CrudRepository<T, LogEntryIdentifier> {

}
