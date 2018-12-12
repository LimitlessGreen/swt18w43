package bioladen.statistic.chart;

import be.ceau.chart.LineChart;
import be.ceau.chart.data.LineData;
import be.ceau.chart.dataset.LineDataset;
import bioladen.datahistory.DataEntry;
import org.salespointframework.time.Interval;

import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LineCharts extends DateTimeCharts {

	LineCharts(ChronoUnit resolution,
			   Interval interval,
			   LinkedHashMap<String, List<DataEntry>> inputMap) {

		super(resolution, interval, inputMap);
	}

	@Override
	public String getJsonCharts() {

		LineData data = new LineData()
				.setLabels(this.getChartLabels());

		for (Map.Entry<String, double[]> chart: this.getChartData().entrySet()) {
			LineDataset dataSet = new LineDataset()
					.setLabel(chart.getKey())
					.setData(chart.getValue())
					.setBorderWidth(2);

			data.addDataset(dataSet);
		}
		return new LineChart(data).toJson();

	}
}
