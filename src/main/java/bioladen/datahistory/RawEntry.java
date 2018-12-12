package bioladen.datahistory;

import java.util.LinkedHashMap;

public interface RawEntry {
	Long getId();

	Double sumUp(String chartName, Double currentValue);

	LinkedHashMap<String, DataHistoryRequest> defineCharts();
}
