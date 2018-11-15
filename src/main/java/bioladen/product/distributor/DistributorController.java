package bioladen.product.distributor;

import org.springframework.stereotype.Controller;

@Controller
public class DistributorController {

	private final DistributorRepository distributorRepository;

	DistributorController(DistributorRepository distributorRepository) {
		this.distributorRepository = distributorRepository;
	}
}
