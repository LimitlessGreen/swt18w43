package bioladen.customer;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Entity
@Table(name = "CUSTOMER")
public class Customer {

	public enum Sex {
		MALE,
		FEMALE,
		VARIOUS
	}

	private static final Map<CustomerType, Double> DISCOUNTS;
	static {
		Map<CustomerType, Double> tempDiscounts = new HashMap<CustomerType, Double>();
		tempDiscounts.put(CustomerType.MANAGER, 0.20);
		tempDiscounts.put(CustomerType.STAFF, 0.15);
		tempDiscounts.put(CustomerType.MAJOR_CUSTOMER, 0.10);
		tempDiscounts.put(CustomerType.HOUSE_CUSTOMER, 0.05);
		DISCOUNTS = Collections.unmodifiableMap(tempDiscounts);
	}

	public static double getDiscount(CustomerType customerType) {
		return DISCOUNTS.getOrDefault(customerType, 0.00);
	}

	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY) // for autoincrement column in database
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	private @Getter
	@Setter
	String firstname;
	private @Getter
	@Setter
	String lastname;
	private @Getter
	@Setter
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
	//TODO: userAcc : UserAccount

	Customer() {
	}

	Customer(String firstname, String lastname, String email, Sex sex, CustomerType customerType) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.sex = sex;
		this.customerType = customerType;
	}


	public boolean isCustomerType(CustomerType customerType) {
		return customerType == this.customerType;
	}

	@Override
	public String toString() {
		return String.format(
				"%s %s: {email: %s, phone: %s, street: %s, type: %s, sex: %s}",
				firstname, lastname, email, phone, street, customerType, sex);
	}
}

