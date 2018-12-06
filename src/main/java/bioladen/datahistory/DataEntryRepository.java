package bioladen.datahistory;

import bioladen.event.EntityLevel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.util.Streamable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;

interface DataEntryRepository
		extends Repository<DataEntry, String>, CrudRepository<DataEntry, String> {

	ArrayList<DataEntry> findAllByOrderBySaveTimeDesc();

	ArrayList<DataEntry> findByEntityLevelAndSaveTimeBetween
			(EntityLevel entityLevel, LocalDateTime from, LocalDateTime to);
}

