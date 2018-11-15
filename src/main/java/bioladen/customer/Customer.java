package bioladen.customer;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.salespointframework.core.AbstractEntity;
import org.salespointframework.useraccount.UserAccount;

@Entity
@Table(name = "CUSTOMER")
public class Customer extends AbstractEntity {

	public enum CustomerType {
		MANAGER,
		STAFF,
		MAJOR_CUSTOMER,
		HOUSE_CUSTOMER
	}

	public enum Sex {
		MALE,
		FEMALE,
		VARIOUS
	}

	static double getDiscount(CustomerType customerType) {
		switch (customerType) {
			case MANAGER: return 0.20;
			case STAFF: return 0.15;
			case MAJOR_CUSTOMER: return 0.15;
			case HOUSE_CUSTOMER: return  0.05;
			default: return 0;
		}
	}

	@EmbeddedId //
	@AttributeOverride(name = "id", column = @Column(name = "CUSTOMER_ID")) //
	private CustomerIdentifier customerIdentifier = new CustomerIdentifier();

	private @Getter @Setter String firstname;
	private @Getter @Setter String lastname;
	private @Getter @Setter String email;
	private @Getter @Setter String phone;
	private @Getter @Setter String street;

	private @Getter Sex sex;
	private @Getter @Setter CustomerType customerType;
	//TODO: userAcc : UserAccount

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
	public CustomerIdentifier getId() {
		return customerIdentifier;
	}

	@Override
	public String toString() {
		return String.format("%s %s: {email: %s, phone: %s, address: %s, type: %s, sex: %s}", firstname, lastname, email, phone, street, customerType, sex);
	}
}

