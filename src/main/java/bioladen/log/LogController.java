package bioladen.log;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogController {
	private final LogRepository logRepository;

	LogController(LogRepository logRepository) {
		this.logRepository = logRepository;
	}

	@GetMapping("/log")
	String log(Model model){

		return "log";
	}
}
