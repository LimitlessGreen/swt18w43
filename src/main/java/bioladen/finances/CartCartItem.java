package bioladen.finances;

import bioladen.product.InventoryProduct;
import lombok.Getter;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Lukas Petzold
 */
@Getter
public class CartCartItem {

	private @Getter final String id;
	private @Getter final BigDecimal price;
	private @Getter final long quantity;
	private @Getter final InventoryProduct inventoryProduct;

	/**
	 * Creates a new {@link CartCartItem}.
	 *
	 * @param inventoryProduct must not be {@literal null}.
	 * @param quantity must not be {@literal null}.
	 */
	CartCartItem(InventoryProduct inventoryProduct, long quantity) {
		this(UUID.randomUUID().toString(), inventoryProduct, quantity);
	}

	/**
	 * Creates a new {@link CartCartItem}.
	 *
	 * @param id must not be {@literal null}.
	 * @param inventoryProduct must not be {@literal null}.
	 * @param quantity must not be {@literal null}.
	 */
	private CartCartItem(String id, InventoryProduct inventoryProduct, long quantity) {

		Assert.notNull(id, "Identifier must not be null!");
		Assert.notNull(inventoryProduct, "InventoryProduct must be not null!");
		Assert.notNull(quantity, "Quantity must be not null!");

		this.id = id;
		this.quantity = quantity;
		this.price = inventoryProduct.getPrice().multiply(BigDecimal.valueOf(quantity));
		this.inventoryProduct = inventoryProduct;
	}


	/**
	 * Returns the name of the inventoryProduct associated with the CartCartItem.
	 *
	 * @return
	 */
	public final String getProductName() {
		return inventoryProduct.getName();
	}

	/**
	 * Returns a new CartCartItem that has the given Quantity added to the current one.
	 *
	 * @param quantity must not be {@literal null}.
	 * @return
	 */
	final CartCartItem add(long quantity) {

		Assert.notNull(quantity, "Quantity must not be null!");

		quantity += this.quantity;
		return new CartCartItem(this.id, this.inventoryProduct, quantity);
	}
	
}
