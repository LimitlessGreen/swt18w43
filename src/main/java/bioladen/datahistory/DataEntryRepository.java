package bioladen.datahistory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;

interface DataEntryRepository
		extends Repository<DataEntry, Long>, CrudRepository<DataEntry, Long> {

	ArrayList<DataEntry<? extends RawEntry>> findAllByOrderById();

	ArrayList<DataEntry<? extends RawEntry>> findAllByOrderByIdDesc();

	ArrayList<DataEntry<? extends RawEntry>> findByEntityLevelAndSaveTimeBetweenOrderBySaveTimeDesc
			(EntityLevel entityLevel, LocalDateTime from, LocalDateTime to);

	DataEntry findTopByEntityAndEntityLevelIsNotIn (RawEntry entity, EntityLevel entityLevel);
}

