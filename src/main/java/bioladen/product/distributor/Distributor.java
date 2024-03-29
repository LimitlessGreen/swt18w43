package bioladen.product.distributor;

import bioladen.datahistory.DataHistoryRequest;
import bioladen.datahistory.RawEntry;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashMap;

/**
 * A distributor.
 * 
 * @author Adrian Kulisch
 */

@Entity
@Table(name = "DISTRIBUTOR")
@NoArgsConstructor
public class Distributor implements RawEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private @Getter Long id;
	
	private @Getter @Setter String name;
	private @Getter @Setter String email;
	private @Getter @Setter String contactName;
	private @Getter @Setter String phone;

	public Distributor(String name, String email, String contactName, String phone) {
		this.name = name;
		this.email = email;
		this.contactName = contactName;
		this.phone = phone;
	}

	@Override
	public String toString() {
		return String.format("%s: {email: %s, contact: %s, phone: %s}", name, email, contactName, phone);
	}

	//TODO: pls implement!
	@Override
	public LinkedHashMap<String, DataHistoryRequest> defineCharts() {
		return null;
	}

	@Override
	public Double sumUp(String chartName, Double currentValue) {
		return null;
	}
}
