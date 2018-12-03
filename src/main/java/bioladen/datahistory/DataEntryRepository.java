package bioladen.datahistory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.LinkedHashSet;

interface DataEntryRepository extends Repository<DataEntry, String>, CrudRepository<DataEntry, String> {
	public ArrayList<DataEntry> findAllByOrderBySaveTimeDesc();
}

