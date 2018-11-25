package bioladen.product.distributor;

import bioladen.event.EntityEvent;
import bioladen.event.EntityLevel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DistributorController implements ApplicationEventPublisherAware {

	private final DistributorRepository distributorRepository;

	DistributorController(DistributorRepository distributorRepository) {
		this.distributorRepository = distributorRepository;
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@GetMapping("/distributorlist")
	String showDistributors(Model model) {
		List<Distributor> distributorList = distributorRepository.findAll();
		model.addAttribute("distributorList", distributorList);

		return "distributorlist";
	}

	@RequestMapping("/distributorform")
	String distributorForm(Model model) {
		return "distributorform";
	}

	@PostMapping("/distributorform")
	String addDistributor(@RequestParam("name")	       String name,
						  @RequestParam("email")	   String email,
						  @RequestParam("contactName") String contactName,
						  @RequestParam("phone")	   String phone) {

		Distributor distributor = new Distributor(name, email, contactName, phone);
		distributorRepository.save(distributor);

		// (👁 ᴥ 👁) Event
		publishEvent(distributor, EntityLevel.CREATED);

		return "redirect:/distributorlist";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@GetMapping("/distributorlist/delete")
	String removeDistributor(@RequestParam("id") Long id) {
		Distributor distributor = distributorRepository.findById(id).get();
		distributorRepository.deleteById(id);

		// (👁 ᴥ 👁) Event
		publishEvent(distributor, EntityLevel.DELETED);

		return "redirect:/distributorlist";
	}

	/*
	 _________________
	< Event publisher >
	 -----------------
        \   ^__^
         \  (@@)\_______
            (__)\       )\/\
                ||----w |
                ||     ||

	*/
	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	private void publishEvent(Distributor distributor, EntityLevel entityLevel) {
		publisher.publishEvent(new EntityEvent<>(distributor, entityLevel));
	}
}
