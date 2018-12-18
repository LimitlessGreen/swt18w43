package bioladen.finances;

import bioladen.product.InventoryProduct;
import bioladen.product.InventoryProductCatalog;
import bioladen.product.distributor_product.DistributorProductCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
class CartControllerTest {

	@Autowired
	WebApplicationContext context;
	@Autowired
	FilterChainProxy securityFilterChain;
	@Autowired
	DistributorProductCatalog distributorProductCatalog;
	@Autowired
	InventoryProductCatalog inventoryProductCatalog;

	private ShoppingCart shoppingCart = new ShoppingCart();

	protected MockMvc mvc;

	@BeforeAll
	public void setUp() {

		context.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);

		mvc = MockMvcBuilders.webAppContextSetup(context).//
				addFilters(securityFilterChain).//
				build();
	}


	@Test
	void wishlistIsAccessibleForPublic() throws Exception {

		mvc.perform(get("/wishlist"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("shoppingCart"));
	}

	@Test
	void wishlistPreventAccessForManager() throws Exception {
		mvc.perform(get("/wishlist").with(user("manager").roles("MANAGER")))
				.andExpect(status().isForbidden());
	}

	@Test
	void addToWishlist() throws Exception {

		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(6L).get(), distributorProductCatalog);
		inventoryProductCatalog.save(inventoryProduct);

		mvc.perform(get("/addToWishlist")
				.param("productId", "1"))
				.andDo(print())
				.andExpect(redirectedUrl("/productlist"));
	}

	@Test
	void deleteFromWishlist() throws Exception {

		InventoryProduct inventoryProduct = new InventoryProduct(distributorProductCatalog.findById(6L).get(), distributorProductCatalog);
		CartCartItem item = new CartCartItem(inventoryProduct, 1);
		String item_id = item.getId();

		mvc.perform(post("/deleteFromWishlist")
				.param("productId", item_id))
				.andExpect(status().isOk());
	}

	@Test
	void addOneToWishlistWithWrongId() throws Exception {
		mvc.perform(post("/addOneToWishlist")
				.param("productId", "1"))
				.andExpect(status().isOk());
	}

	@Test
	void removeOneFromWishlistWithWrongId() throws Exception {
		mvc.perform(post("/removeOneFromWishlist")
				.param("productId", "1"))
				.andExpect(status().isOk());
	}


}