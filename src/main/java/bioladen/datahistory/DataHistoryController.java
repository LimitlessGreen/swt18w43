package bioladen.datahistory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.reflect.Field;

@Controller
public class DataHistoryController {
	private final DataEntryRepository dataEntryRepository;

	DataHistoryController(DataEntryRepository dataEntryRepository) {
		this.dataEntryRepository = dataEntryRepository;
	}
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/datahistory")
	String datahistory(Model model){
		model.addAttribute("history", dataEntryRepository.findAllByOrderBySaveTimeDesc());

		return "datahistory";
	}
}
