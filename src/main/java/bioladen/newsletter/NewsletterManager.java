package bioladen.newsletter;

import lombok.RequiredArgsConstructor;
import org.salespointframework.time.BusinessTime;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class NewsletterManager {

	private final BusinessTime businessTime;
	private final NewsletterRepository newsletterRepository;

	public void subscribe(String mail) {
		if (newsletterRepository.existsBySubscribedIsFalseAndMailEquals(mail)) {

			Newsletter newsletter = newsletterRepository.findByMail(mail);
			newsletter.setUnsubscribeId(String.format("%s:%s", mail, businessTime.getTime()));
			newsletter.setSubscribed(true);

			newsletterRepository.save(newsletter);

		} else if (!newsletterRepository.existsByMail(mail)) {

			Newsletter newsletter = new Newsletter(mail);
			newsletter.setUnsubscribeId(String.format("%s:%s", mail, businessTime.getTime()));
			newsletter.setSubscribed(true);

			newsletterRepository.save(newsletter);
		}
	}

	public void unsubscribe(String mail) {
		if (newsletterRepository.existsByMail(mail)) {

			Newsletter newsletter = newsletterRepository.findByMail(mail);
			newsletter.setSubscribed(false);
			newsletter.setUnsubscribeId(null);

			newsletterRepository.save(newsletter);
		}
	}

	public ArrayList<Newsletter> getSubscribed() {
		return newsletterRepository.findBySubscribedTrue();
	}

}
