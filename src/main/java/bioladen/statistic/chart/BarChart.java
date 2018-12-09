package bioladen.statistic.chart;

import bioladen.datahistory.DataEntry;
import org.salespointframework.time.Interval;

import java.time.temporal.ChronoUnit;
import java.util.List;

public class BarChart extends DateTimeChart {

	BarChart (ChronoUnit resolution, Interval interval, List<DataEntry> dataList) {
		super(resolution, interval, dataList);
	}
}
