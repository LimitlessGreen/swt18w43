package bioladen.statistic.chart;

import be.ceau.chart.LineChart;
import be.ceau.chart.color.Color;
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

		Color color = Color.random();
		for (Map.Entry<String, double[]> chart: this.getChartData().entrySet()) {

			final int colorRandomizingOffset = 150;

			color = new Color(
					(color.getR() + colorRandomizingOffset) % 0x100,
					(color.getG() + colorRandomizingOffset) % 0x100,
					(color.getB() + colorRandomizingOffset) % 0x100);

			LineDataset dataSet = new LineDataset()
					.setLabel(chart.getKey())
					.setData(chart.getValue())
					.setBorderWidth(2)
					.setBorderColor(color)
					.setBackgroundColor(color);

			data.addDataset(dataSet);
		}
		return new LineChart(data).toJson();

	}
}
