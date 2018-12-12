package bioladen.statistic.chart;

import bioladen.datahistory.DataEntry;
import bioladen.datahistory.DataHistoryManager;
import bioladen.datahistory.DataHistoryRequest;
import bioladen.datahistory.RawEntry;
import lombok.RequiredArgsConstructor;
import org.salespointframework.time.Interval;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChartFactory {

	private final DataHistoryManager dataHistoryManager;

	public Charts getPieChart() {
		return new PieCharts();
	}

	public LineCharts getBarChart(ChronoUnit resolution, Interval interval, RawEntry entry) {

		LinkedHashMap<String, List<DataEntry>> inputMap = new LinkedHashMap<>();

		for (Map.Entry<String, DataHistoryRequest> mapEntry : entry.defineCharts().entrySet()) {
			LinkedList list = getRequest(mapEntry.getValue(), interval);
			inputMap.put(mapEntry.getKey(), list);
		}
		return new LineCharts(resolution, interval, inputMap);
	}

	private LinkedList getRequest(DataHistoryRequest request, Interval interval) {
		return dataHistoryManager.findBy(
				request.getClazz(),
				request.getEntityLevel(),interval);
	}
}


