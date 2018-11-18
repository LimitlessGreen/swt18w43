package bioladen.product.distributor;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import org.salespointframework.core.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * A distributor.
 * 
 * @author Adrian Kulisch
 */

@NoArgsConstructor
public class Distributor {
	
	@Id
	private @Getter String distributorIdentifier;
	
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
}
