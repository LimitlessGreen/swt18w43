package bioladen.statistic;

import bioladen.customer.Customer;
import bioladen.datahistory.DataHistoryManager;
import bioladen.datahistory.EntityLevel;
import bioladen.statistic.chart.Chart;
import bioladen.statistic.chart.ChartFactory;
import lombok.RequiredArgsConstructor;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.time.Interval;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.LinkedList;

@Controller
@RequiredArgsConstructor
public class StatisticController {

	private final BusinessTime businessTime;
	private final DataHistoryManager dataHistoryManager;

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/statistic")
	String datahistory(Model model){

		LinkedList listAdded = dataHistoryManager.findBy
				(Customer.class,
						EntityLevel.CREATED,
						Interval.from(businessTime.getTime().minusMonths(1)).to(businessTime.getTime()));

		LinkedList listDeleted = dataHistoryManager.findBy
				(Customer.class,
						EntityLevel.DELETED,
						Interval.from(businessTime.getTime().minusMonths(1)).to(businessTime.getTime()));

		ChartFactory chartFactory = new ChartFactory();
		Chart customerAddedChart = chartFactory.getBarChart(
				ChronoUnit.MINUTES, Interval.from(businessTime.getTime().minusHours(1)).to(businessTime.getTime()), listAdded);
		customerAddedChart.fillChart();

		Chart customerDeletedChart = chartFactory.getBarChart(
				ChronoUnit.MINUTES, Interval.from(businessTime.getTime().minusHours(1)).to(businessTime.getTime()), listDeleted);
		customerDeletedChart.fillChart();

		LinkedHashMap chartMap1 = customerAddedChart.getChartMap();
		LinkedHashMap chartMap2 = customerDeletedChart.getChartMap();

		model.addAttribute("labels", chartMap1.keySet());
		model.addAttribute("data1", chartMap1.values());
		model.addAttribute("data2", chartMap2.values());

		return "statistic";
	}
}
