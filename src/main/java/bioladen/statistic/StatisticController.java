package bioladen.statistic;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticController {

	StatisticController() {
	}

	@GetMapping("/statistic")
	String datahistory(Model model){

		return "statistic";
	}
}
