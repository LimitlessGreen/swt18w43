package bioladen.statistic.chart;

import bioladen.datahistory.DataEntry;
import lombok.AccessLevel;
import lombok.Getter;
import org.salespointframework.time.Interval;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoField.DAY_OF_WEEK;

public abstract class DateTimeCharts implements Charts {

	private ChronoUnit currentResolution;
	private Interval currentInterval;
	private LinkedHashMap<String, List<DataEntry>> inputMap;
	private LinkedHashMap<LocalDateTime, Double> zeroMap;

	@Getter(AccessLevel.PROTECTED)
	private LinkedHashMap<String, double[]> chartData = new LinkedHashMap<>();

	private LinkedList<String> chartLabels = new LinkedList<>();

	DateTimeCharts(ChronoUnit resolution, Interval interval, LinkedHashMap<String, List<DataEntry>> inputMap) {
		this.currentResolution = resolution;
		this.currentInterval = interval;
		this.inputMap = inputMap;

		this.zeroMap = generateRawMap(this.currentResolution, this.currentInterval);
		fillCharts();
	}

	private void fillCharts() {

		for (Map.Entry<String, List<DataEntry>> inputChart : this.inputMap.entrySet()) {

			LinkedHashMap<LocalDateTime, Double> chart = new LinkedHashMap<>(this.zeroMap);

			for (DataEntry entry : inputChart.getValue()) {

				LocalDateTime truncatedTime = truncate(entry.getSaveTime(), this.currentResolution);

				Double value = 0D;

				if (chart.containsKey(truncatedTime)) {
					value = chart.get(truncatedTime);
					value = entry.getEntity().sumUp(inputChart.getKey(), value);
					chart.put(truncatedTime, value);
				}
			}

			LinkedList<Double> outputData = new LinkedList<>(chart.values());

			this.chartData.put(inputChart.getKey(), doubleConverter(outputData));
		}
	}

	private LinkedHashMap<LocalDateTime, Double> generateRawMap(ChronoUnit resolution, Interval interval) {

		final long daysPerWeek = 7;
		final long daysPerMonth = 30L;
		final double zero = 0D;

		LinkedHashMap<LocalDateTime, Double> output = new LinkedHashMap<>();

		long duration;
		switch (resolution) {
			case MINUTES:
				duration = interval.getDuration().toMinutes();
				break;
			case HOURS:
				duration = interval.getDuration().toHours();
				break;
			case DAYS:
				duration = interval.getDuration().toDays();
				break;
			case WEEKS:
				duration = interval.getDuration().toDays() / daysPerWeek;
				break;
			case MONTHS:
				duration = interval.getDuration().toDays() / daysPerMonth;
				break;
			default:
				throw new UnsupportedOperationException(resolution.toString());
		}

		for (long i = 0; i < duration; i++) {
			LocalDateTime time = null;

			switch (resolution) {
				case MINUTES:
					time = truncate(interval.getEnd().minusMinutes(duration - i), resolution);
					break;
				case HOURS:
					time = truncate(interval.getEnd().minusHours(duration - i), resolution);
					break;
				case DAYS:
					time = truncate(interval.getEnd().minusDays(duration - i), resolution);
					break;
				case WEEKS:
					time = truncate(interval.getEnd().minusWeeks(duration - i), resolution);
					break;
				case MONTHS:
					time = truncate(interval.getEnd().minusMonths(duration - i), resolution);
					break;
				default:
					throw new UnsupportedOperationException(resolution.toString());
			}
			output.put(time, zero);
			this.chartLabels.add(time.toString());
		}

		return output;

	}

	private LocalDateTime truncate(LocalDateTime time, ChronoUnit resolution) {

		switch (resolution) {
			case WEEKS:
				return time.with(DAY_OF_WEEK, DayOfWeek.of(1).getValue()).truncatedTo(ChronoUnit.DAYS);
			case MONTHS:
				return time.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
			default:
				return time.truncatedTo(resolution);
		}
	}

	private double[] doubleConverter(LinkedList<Double> list) {
		int size = list.size();
		double[] result = new double[size];
		Double[] temp = list.toArray(new Double[size]);
		for (int n = 0; n < size; ++n) {
			result[n] = temp[n];
		}
		return result;
	}

	String[] getChartLabels() {
		return this.chartLabels.toArray(new String[chartLabels.size()]);
	}

	@Override
	public int size(){
		return chartData.size();
	}
}