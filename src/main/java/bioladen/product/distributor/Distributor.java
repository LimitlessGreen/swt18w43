package bioladen.product.distributor;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.salespointframework.core.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * A distributor.
 * 
 * @author Adrian Kulisch
 */

@Entity
@Table(name = "DISTRIBUTOR")
public class Distributor extends AbstractEntity<DistributorIdentifier> {
	
	@EmbeddedId
	@AttributeOverride(name = "id", column = @Column(name = "DISTRIBUTOR_ID")) //
	private DistributorIdentifier distributorIdentifier = new DistributorIdentifier();
	
	private @Getter @Setter String name;
	private @Getter @Setter String email;
	private @Getter @Setter String contactName;
	private @Getter @Setter String phone;

	public Distributor() {
	}

	public Distributor(String name, String email, String contactName, String phone) {
		this.name = name;
		this.email = email;
		this.contactName = contactName;
		this.phone = phone;
	}
	
	@Override
	public DistributorIdentifier getId() {
		return distributorIdentifier;
	}
}
