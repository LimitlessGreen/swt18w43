package bioladen.statistic;

import bioladen.customer.Customer;
import bioladen.datahistory.DataHistoryManager;
import bioladen.finances.ShoppingCartCancel;
import bioladen.finances.ShoppingCartSale;
import bioladen.product.InventoryProduct;
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

		LineCharts cancelsChart = chartFactory.getBarChart(
				resolution,
				interval,
				new ShoppingCartCancel()
		);

		LineCharts productsChart = chartFactory.getBarChart(
				resolution,
				interval,
				new InventoryProduct()
		);


		dataTable.put("Benutzer erstellt", String.valueOf(customerStatistic.amountOfCustomersCreatedBetween(interval)));
		dataTable.put("Benutzer gelöscht", String.valueOf(customerStatistic.amountOfCustomersDeletedBetween(interval)));
		dataTable.put("Anzahl Einkäufe", String.valueOf(financesStatistic.amountOfSalesBetween(interval)));
		dataTable.put("Anzahl Stornierungen", String.valueOf(financesStatistic.amountOfCancelsBetween(interval)));

		charts.put("customerChart", customerChart.getJsonCharts());
		charts.put("salesChart", salesChart.getJsonCharts());
		charts.put("cancelsChart", cancelsChart.getJsonCharts());
		charts.put("productsChart", productsChart.getJsonCharts());

		model.addAttribute("charts", charts);
		model.addAttribute("dataTable", dataTable);
		
		return "statistic";
	}
}
