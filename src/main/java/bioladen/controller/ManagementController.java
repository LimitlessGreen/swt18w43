package bioladen.controller;

import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Controller;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Controller
public class ManagementController {
	@RequestMapping("/administration")
	public String admin() {
		return "administration";
	}
}
