package bioladen.newsletter;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface NewsletterRepository extends CrudRepository<Newsletter, Long> {

	ArrayList<Newsletter> findBySubscribedTrue();

	Newsletter findByMail(String mail);

	boolean existsByMail(String mail);

	boolean existsBySubscribedIsFalseAndMailEquals(String mail);


}
