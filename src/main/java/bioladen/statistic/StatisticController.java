package bioladen.statistic;

import bioladen.customer.Customer;
import bioladen.datahistory.DataHistoryManager;
import bioladen.finances.ShoppingCartSale;
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
	private final CustomerStatistic customerStatistic;
	private final FinancesStatistic financesStatistic;

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/statistic")
	String datahistory(Model model){

		LinkedHashMap<String, String> charts = new LinkedHashMap<>();
		LinkedHashMap<String, String> dataTable = new LinkedHashMap<>();

		Interval interval = Interval.from(businessTime.getTime().minusHours(1)).to(businessTime.getTime());
		ChronoUnit resolution = ChronoUnit.MINUTES;

		LineCharts customerChart = chartFactory.getBarChart(
				resolution,
				interval,
				new Customer()
		);

		LineCharts salesChart = chartFactory.getBarChart(
				resolution,
				interval,
				new ShoppingCartSale()
		);


		dataTable.put("Benutzer erstellt", customerStatistic.customersCreatedBetween(interval).toString());
		dataTable.put("Benutzer gelöscht", customerStatistic.customersDeletedBetween(interval).toString());
		dataTable.put("Anzahl Einkäufe", financesStatistic.salesBetween(interval).toString());
		dataTable.put("Anzahl Stornierungen", financesStatistic.cancelsBetween(interval).toString());

		charts.put("customerChart", customerChart.getJsonCharts());
		charts.put("salesChart", salesChart.getJsonCharts());

		model.addAttribute("charts", charts);
		model.addAttribute("dataTable", dataTable);

		model.addAttribute("customerJson", customerChart.getJsonCharts());
		model.addAttribute("salesJson", salesChart.getJsonCharts());

		return "statistic";
	}
}
