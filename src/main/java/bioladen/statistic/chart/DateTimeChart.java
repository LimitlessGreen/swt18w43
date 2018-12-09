package bioladen.statistic.chart;

import bioladen.datahistory.DataEntry;
import lombok.Getter;
import org.salespointframework.time.Interval;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;

import static java.time.temporal.ChronoField.DAY_OF_WEEK;

public abstract class DateTimeChart implements Chart {

	private ChronoUnit currentResolution;
	private Interval currentInterval;
	private List<DataEntry> dataList;

	@Getter
	protected LinkedHashMap<LocalDateTime, Double> chartMap = new LinkedHashMap<>();

	DateTimeChart (ChronoUnit resolution, Interval interval, List<DataEntry> dataList) {
		this.currentResolution = resolution;
		this.currentInterval = interval;
		this.dataList = dataList;
	}

	@Override
	public void fillChart () {

		this.chartMap = generateRawMap(this.currentResolution, this.currentInterval);

		for (DataEntry entry : this.dataList) {
			LocalDateTime truncatedTime = truncate(entry.getSaveTime(), this.currentResolution);
			if (chartMap.containsKey(truncatedTime)) {
				Double value = chartMap.get(truncatedTime) + 1;
				chartMap.put(truncatedTime, value);
			}
		}
	}

	private LinkedHashMap<LocalDateTime, Double> generateRawMap(ChronoUnit resolution, Interval interval) {

		long daysPerWeek = 7;
		long daysPerMonth = 30L;
		double zero = 0D;

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
			switch (resolution) {
				case MINUTES:
					output.put(truncate(interval.getEnd().minusMinutes(duration - i), resolution), zero);
					break;
				case HOURS:
					output.put(truncate(interval.getEnd().minusHours(duration - i), resolution), zero);
					break;
				case DAYS:
					output.put(truncate(interval.getEnd().minusDays(duration - i), resolution), zero);
					break;
				case WEEKS:
					output.put(truncate(interval.getEnd().minusWeeks(duration - i), resolution), zero);
					break;
				case MONTHS:
					output.put(truncate(interval.getEnd().minusMonths(duration - i), resolution), zero);
					break;
				default:
					throw new UnsupportedOperationException(resolution.toString());
			}
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
}