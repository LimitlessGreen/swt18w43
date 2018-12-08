package bioladen.product.distributor_product;

import bioladen.datahistory.DataHistoryManager;
import bioladen.datahistory.EntityLevel;
import bioladen.product.MwStCategory;
import bioladen.product.Organization;
import bioladen.product.ProductCategory;
import bioladen.product.distributor.Distributor;
import bioladen.product.distributor.DistributorRepository;
import lombok.RequiredArgsConstructor;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DistributorProductController {

	private final DistributorProductCatalog distributorProductCatalog;
	private final DistributorRepository distributorRepository;
	private final AuthenticationManager authenticationManager;

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@GetMapping("/distributorproductlist")
	String showDistributorProducts(Model model) {
		List<DistributorProduct> distributorProductList = distributorProductCatalog.findAll();
		List<Distributor> distributorList = distributorRepository.findAll();

		model.addAttribute("distributorProductList", distributorProductList);
		model.addAttribute("distributorList", distributorList);
		model.addAttribute("productCategories", ProductCategory.values());
		model.addAttribute("mwStCategories", MwStCategory.values());
		model.addAttribute("organizations", Organization.values());

		return "distributorproductlist";
	}

	@RequestMapping("/addDistributorProduct")
	String distributorForm(Model model) {
		List<Distributor> distributorList = distributorRepository.findAll();
		model.addAttribute("distributorList", distributorList);
		model.addAttribute("productCategories", ProductCategory.values());
		model.addAttribute("mwStCategories", MwStCategory.values());
		model.addAttribute("organizations", Organization.values());

		return "distributorproductform";
	}

	@PostMapping("/addDistributorProduct")
	String addDistributor(@RequestParam("name")	              String          name,
						  @RequestParam("distributor")        Long            distributorId,
						  @RequestParam("price")              String          priceString,
						  @RequestParam("unit")               String          unitString,
						  @RequestParam("minimumOrderAmount") long            minimumOrderAmount,
						  @RequestParam("productCategory")    ProductCategory productCategory,
						  @RequestParam("mwStCategory")       MwStCategory    mwStCategory,
						  @RequestParam("pfandPrice")         String          pfandPriceString,
						  @RequestParam("organization")       Organization    organization) {

		BigDecimal price      = new BigDecimal(priceString);
		BigDecimal unit       = new BigDecimal(unitString);
		BigDecimal pfandPrice = new BigDecimal(pfandPriceString);

		Distributor distributor = distributorRepository.findById(distributorId).get();

		DistributorProduct distributorProduct = new DistributorProduct(name,
				                                                       distributor,
				                                                       price,
				                                                       unit,
				                                                       minimumOrderAmount,
				                                                       productCategory,
				                                                       mwStCategory,
				                                                       pfandPrice,
				                                                       organization);
		distributorProductCatalog.save(distributorProduct);

		// (👁 ᴥ 👁) Event
		pushDistributorProduct(distributorProduct, EntityLevel.CREATED);

		return "redirect:/distributorproductlist";
	}

	@PostMapping("/importBnn")
	String readBnn(@RequestParam("distributor") Long          distributorId,
				   @RequestParam("bnnFile")     MultipartFile bnnFile        ) throws IOException {
		Distributor distributor = distributorRepository.findById(distributorId).get();

		List<String> csv = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new InputStreamReader(bnnFile.getInputStream()));
		for (Object line : br.lines().toArray()) {
			String[] lineFields = line.toString().split(";");
			distributorProductCatalog.save(new DistributorProduct(
					lineFields[0], distributor, new BigDecimal(lineFields[1]), new BigDecimal(lineFields[2]),
					Long.valueOf(lineFields[3]), ProductCategory.valueOf(lineFields[4]), MwStCategory.valueOf(lineFields[5]),
					new BigDecimal(lineFields[6]), Organization.valueOf(lineFields[7])
				));
		}

		return "redirect:/distributorproductlist";
	}

//	@PostMapping("/removeDistributor")
//	String removeDistributor(@RequestParam("distributorIdentifier") String id) {
//		distributorRepository.deleteById(id);
//
//		return "redirect:/distributorlist";
//	}

	/* TODO: Event for distributor product deletions
	 _________________
	< Event publisher >
	 -----------------
        \   ^__^
         \  (@@)\_______
            (__)\       )\/\
                ||----w |
                ||     ||

	*/
	private final DataHistoryManager<DistributorProduct> dataHistoryManager;

	private void pushDistributorProduct(DistributorProduct distributorProduct, EntityLevel entityLevel) {
		Optional<UserAccount> currentUser = this.authenticationManager.getCurrentUser();
		dataHistoryManager.push(
				distributorProduct.getName(),
				distributorProduct,
				entityLevel,
				currentUser.orElse(null));
	}
}
