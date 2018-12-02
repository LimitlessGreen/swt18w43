package bioladen.datahistory;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;

interface DataEntryRepository extends CrudRepository<DataEntry, String> {
	public LinkedHashSet<DataEntry> findAll();
}
