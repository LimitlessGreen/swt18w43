package bioladen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ManagementController {
	@RequestMapping("/administration")
	public String admin() {
		return "administration";
	}
}
