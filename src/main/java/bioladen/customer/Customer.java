package bioladen.customer;

import bioladen.datahistory.DataHistoryRequest;
import bioladen.datahistory.EntityLevel;
import bioladen.datahistory.RawEntry;
import bioladen.product.InventoryProduct;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * a Customer
 *
 * @author Lisa Riedel
 */

@Entity
@Table(name = "CUSTOMER")
public class Customer implements RawEntry {


	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY) // for autoincrement column in database
	@Column(name = "id", updatable = false, nullable = false)
	private @Setter Long id;

	private @Getter
	@Setter
	String firstname;
	private @Getter
	@Setter
	String lastname;
	private @Getter
	@Setter
	@Column(unique=true)
	String email;
	private @Getter
	@Setter
	String phone;
	private @Getter
	@Setter
	String street;


	private @Getter
	Sex sex;
	private @Getter
	@Setter
	CustomerType customerType;
	
	private LinkedHashMap<String, Integer> purchasedProducts = new LinkedHashMap<>();

	/**
	 * empty Constructor
	 */
	public Customer() {
	}

	/**
	 * Creates a new {@link Customer}.
	 * @param firstname must not be {@literal null}
	 * @param lastname must not be {@literal null}
	 * @param email must not be {@literal null}
	 * @param sex must not be {@literal null}
	 * @param customerType must not be {@literal null}
	 */
	public Customer(String firstname, String lastname, String email, Sex sex, CustomerType customerType) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.sex = sex;
		this.customerType = customerType;
	}

	/**
	 * Checks if Customer is a certain type
	 * @param customerType must not be {@literal null}
	 * @return boolean
	 */
	public boolean isCustomerType(CustomerType customerType) {
		return customerType == this.customerType;
	}

	/**
	 *Add purchases to a specific customer (one per purchase, not amount of products)
	 */
	public void addPurchase(InventoryProduct inventoryProduct) {
		int amount = (this.purchasedProducts.containsKey(inventoryProduct.getName()))
				? this.purchasedProducts.get(inventoryProduct.getName()) + 1
				: 0;
		this.purchasedProducts.put(inventoryProduct.getName(), amount);
	}

	/**
	 * get the product, max purchased for a specific customer.
	 * @return InventoryProduct
	 */
	public String getMaxPurchasedProduct() {
		int maxValue = 0;
		String tmpProduct = null;
		for (Map.Entry<String, Integer> entry : this.purchasedProducts.entrySet()) {
			if (entry.getValue() >= maxValue) {
				maxValue = entry.getValue();
				tmpProduct = entry.getKey();
			}
		}
		return tmpProduct;
	}

	/**
	 *Convert lastname and firstname into a String
	 *@return String
	 */
	public String getName() {
		return this.lastname + ", " + this.firstname;
	}

	/**
	 * Convert a Customer in a String for {@link bioladen.datahistory.DataHistoryLogger}
	 * @return String
	 */

	@Override
	public String toString() {
		return String.format(
				"%s %s: {email: %s, phone: %s, street: %s, type: %s, sex: %s}",
				firstname, lastname, email, phone, street, customerType, sex);
	}

	@Override
	public LinkedHashMap<String, DataHistoryRequest> defineCharts() {
		LinkedHashMap<String, DataHistoryRequest> output = new LinkedHashMap<>();

		output.put("Benutzer erstellt", new DataHistoryRequest(Customer.class, EntityLevel.CREATED));
		output.put("Benutzer gelöscht", new DataHistoryRequest(Customer.class, EntityLevel.DELETED));
		return output;
	}

	@Override
	public Double sumUp(String chartName, Double currentValue) {
		if (chartName.equals("Benutzer erstellt") || chartName.equals("Benutzer gelöscht")) {

			return currentValue + 1D;
		}
		return currentValue + 1D;
	}
}

