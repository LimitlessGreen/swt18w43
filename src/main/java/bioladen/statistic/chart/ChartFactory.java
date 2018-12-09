package bioladen.statistic.chart;

import bioladen.datahistory.DataEntry;
import org.salespointframework.time.Interval;

import java.time.temporal.ChronoUnit;
import java.util.List;

public class ChartFactory {

	public Chart getPieChart() {
		return new PieChart();
	}

	public Chart getBarChart(ChronoUnit resolution, Interval interval, List<DataEntry> dataList) {
		return new BarChart(resolution, interval, dataList);
	}
}


