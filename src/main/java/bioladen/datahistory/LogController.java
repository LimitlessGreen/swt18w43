package bioladen.datahistory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogController {
	private final DataEntryRepository dataEntryRepository;

	LogController(DataEntryRepository dataEntryRepository) {
		this.dataEntryRepository = dataEntryRepository;
	}

	@GetMapping("/datahistory")
	String log(Model model){

		return "datahistory";
	}
}
