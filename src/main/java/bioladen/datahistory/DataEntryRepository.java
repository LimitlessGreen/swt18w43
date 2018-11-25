package bioladen.datahistory;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

interface DataEntryRepository extends CrudRepository<DataEntry, String> {
	public ArrayList<DataEntry> findAll();
}
