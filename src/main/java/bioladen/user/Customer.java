package bioladen.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.salespointframework.useraccount.UserAccount;

@Entity
public class Customer {
	private @Id @GeneratedValue long id;

	private String address;

	// (｡◕‿◕｡)
	// Jedem Customer ist genau ein UserAccount zugeordnet, um später über den UserAccount an den Customer zu kommen,
	// speichern wir den hier
	@OneToOne //
	private UserAccount userAccount;

	@SuppressWarnings("unused")
	private Customer() {}

	public Customer(UserAccount userAccount, String address) {
		this.userAccount = userAccount;
		this.address = address;
	}

	public long getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}
}
