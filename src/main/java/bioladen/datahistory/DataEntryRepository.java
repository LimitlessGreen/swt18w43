package bioladen.datahistory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;

interface DataEntryRepository
		extends Repository<DataEntry, Long>, CrudRepository<DataEntry, Long> {

	ArrayList<DataEntry> findAllByOrderById();

	ArrayList<DataEntry> findAllByOrderByIdDesc();

	ArrayList<DataEntry> findByEntityLevelAndSaveTimeBetweenOrderBySaveTimeDesc
			(EntityLevel entityLevel, LocalDateTime from, LocalDateTime to);

	DataEntry findTopByEntityAndEntityLevelIsNotIn (RawEntry rawEntry, EntityLevel entityLevel);
}

