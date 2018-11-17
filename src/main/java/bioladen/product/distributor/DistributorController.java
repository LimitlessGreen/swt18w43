package bioladen.product.distributor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DistributorController {

	private final DistributorRepository distributorRepository;

	DistributorController(DistributorRepository distributorRepository) {
		this.distributorRepository = distributorRepository;
	}

	@GetMapping("/distributorlist")
	String customerRepository (Model model) {
		List<Distributor> distributorList = distributorRepository.findAll();
		model.addAttribute("distributorList", distributorList);

		return "distributorlist";
	}
}
