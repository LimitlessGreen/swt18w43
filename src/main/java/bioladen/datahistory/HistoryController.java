package bioladen.datahistory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HistoryController {
	private final DataEntryRepository dataEntryRepository;

	HistoryController(DataEntryRepository dataEntryRepository) {
		this.dataEntryRepository = dataEntryRepository;
	}

	@GetMapping("/datahistory")
	String datahistory(Model model){

		return "datahistory";
	}
}
