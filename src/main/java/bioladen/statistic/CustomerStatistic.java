package bioladen.statistic;

import bioladen.customer.Customer;
import bioladen.datahistory.DataHistoryManager;
import bioladen.event.EntityLevel;
import lombok.RequiredArgsConstructor;
import org.salespointframework.time.Interval;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public  class CustomerStatistic {

	private final DataHistoryManager dataHistoryManager;

	/*------------------------*/
	/*  1. SIMPLE AMOUNTS
	/*------------------------*/

	int amountOfCustomersCreatedBetween(Interval interval) {
		return dataHistoryManager.findBy(Customer.class, EntityLevel.CREATED, interval).size();
	}

	int amountOfCustomersDeletedBetween(Interval interval) {
		return dataHistoryManager.findBy(Customer.class, EntityLevel.DELETED, interval).size();
	}

	int amountOfCustomersModifiedBetween(Interval interval){
		return dataHistoryManager.findBy(Customer.class, EntityLevel.MODIFIED, interval).size();

	}

}
