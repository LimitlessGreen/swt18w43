package bioladen.product.distributor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DistributorController {

	private final DistributorRepository distributorRepository;

	DistributorController(DistributorRepository distributorRepository) {
		this.distributorRepository = distributorRepository;
	}

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

		return "redirect:/distributorlist";
	}

	@GetMapping("/distributorlist/delete")
	String removeDistributor(@RequestParam("id") String id) {
		distributorRepository.deleteById(id);

		return "redirect:/distributorlist";
	}
}
