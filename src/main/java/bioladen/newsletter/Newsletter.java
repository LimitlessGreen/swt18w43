package bioladen.newsletter;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.security.MessageDigest;

@Entity
@Table(name = "NEWSLETTER")
public class Newsletter {

	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY) // for autoincrement column in database
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	private @Getter @Setter String mail;

	@Setter
	private boolean subscribed;

	private String unsubscribeId = null;

	public Newsletter(String mail) {
		this.mail = mail;
	}

	public void setUnsubscribeId(String unsubscribeId) {
		sha512Converter(unsubscribeId);
	}

	private boolean hasUnsubscribeId() {
		return unsubscribeId != null;
	}

	private String sha512Converter(String input) {
		StringBuilder sb = new StringBuilder();
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] data = md.digest(input.getBytes());

			for (byte aData : data) {
				sb.append(Integer.toString((aData & 0xff) + 0x100, 16).substring(1));
			}
		}
		catch(Exception e) { }

		return (sb.length() != 0) ? sb.toString() : null;
	}
}
