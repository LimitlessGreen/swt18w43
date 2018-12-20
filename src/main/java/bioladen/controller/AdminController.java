package bioladen.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@RequestMapping("/admin")
	public String admin() {

		return "admin";
	}
}
