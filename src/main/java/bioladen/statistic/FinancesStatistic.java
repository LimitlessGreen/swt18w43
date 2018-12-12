package bioladen.statistic;

import bioladen.datahistory.DataHistoryManager;
import bioladen.datahistory.EntityLevel;
import bioladen.finances.ShoppingCartCancel;
import bioladen.finances.ShoppingCartSale;
import lombok.RequiredArgsConstructor;
import org.salespointframework.time.Interval;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public  class FinancesStatistic {

	private final DataHistoryManager dataHistoryManager;

	/*------------------------*/
	/*  1. SIMPLE AMOUNTS
	/*------------------------*/

	int amountOfSalesBetween(Interval interval) {
		return salesBetween(interval).size();
	}

	int amountOfCancelsBetween(Interval interval) {
		return cancelsBetween(interval).size();
	}

	/*------------------------*/
	/*  2. Data Lists
	/*------------------------*/

	LinkedList salesBetween(Interval interval) {
		return dataHistoryManager.findBy(ShoppingCartSale.class, EntityLevel.CREATED, interval);
	}

	LinkedList cancelsBetween(Interval interval) {
		return dataHistoryManager.findBy(ShoppingCartCancel.class, EntityLevel.DELETED, interval);
	}

}

