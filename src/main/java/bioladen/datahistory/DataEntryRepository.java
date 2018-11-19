package bioladen.datahistory;

import org.springframework.data.mongodb.repository.MongoRepository;

interface DataEntryRepository<T extends DataEntry> extends MongoRepository<T, String> {

}
