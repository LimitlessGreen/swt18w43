package bioladen.product.distributor;

import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;

@Component
public class DistributorDataInitializer implements DataInitializer {
	private final DistributorRepository distributorRepository;

	DistributorDataInitializer(DistributorRepository distributorRepository) {
		this.distributorRepository = distributorRepository;
	}

	@Override
	public void initialize() {
		distributorRepository.save(
				new Distributor(
						"Bauer Heinze",
						"heinze@bauern.de",
						"Heinz Heinze",
						"01524506154"
				)
		);

		distributorRepository.save(
				new Distributor(
						"Fridas Hof",
						"frida@fridas-hof.de",
						"Frida Fritzsche",
						"01736825268"
				)
		);
	}
}
