package bioladen.statistic;

import bioladen.customer.Customer;
import bioladen.datahistory.DataHistoryManager;
import bioladen.statistic.chart.ChartFactory;
import bioladen.statistic.chart.LineCharts;
import lombok.RequiredArgsConstructor;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.time.Interval;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;

@Controller
@RequiredArgsConstructor
public class StatisticController {

	private final BusinessTime businessTime;
	private final DataHistoryManager dataHistoryManager;
	private final ChartFactory chartFactory;

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/statistic")
	String datahistory(Model model){

		LinkedHashMap<String, String> charts = new LinkedHashMap<>();

		LineCharts customerChart = chartFactory.getBarChart(
				ChronoUnit.MINUTES,
				Interval.from(businessTime.getTime().minusHours(1)).to(businessTime.getTime()),
				new Customer());

		charts.put("customerChart", customerChart.getJsonCharts());

		model.addAttribute("charts", charts);

		model.addAttribute("customerJson", customerChart.getJsonCharts());

		return "statistic";
	}
}
