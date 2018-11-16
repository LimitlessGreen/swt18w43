/**
 * 
 */
package bioladen.product;

import org.salespointframework.core.AbstractEntity;

import javax.persistence.*;

/**
 * @author Adrian Kulisch
 */

@Entity
@Table(name = "DISTRIBUTOR_PRODUCT")
public class DistributorProduct extends AbstractEntity<DistributorProductIdentifier> {

	@EmbeddedId
	@AttributeOverride(name = "id", column = @Column(name = "DISTRIBUTOR_PRODUCT_ID")) //
	private DistributorProductIdentifier distributorProductIdentifier = new DistributorProductIdentifier();

	@Override
	public DistributorProductIdentifier getId() {
		return distributorProductIdentifier;
	}
}
