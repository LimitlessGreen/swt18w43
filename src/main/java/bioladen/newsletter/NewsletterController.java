package bioladen.newsletter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Controller
@RequiredArgsConstructor
public class NewsletterController {

	private final NewsletterRepository newsletterRepository;
	private final NewsletterManager newsletterManager;

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@RequestMapping("/admin")
	public String admin() {

		return "admin";
	}

	@RequestMapping("/cancel_newsletter")
	public String cancelNewsletter() {
		return "cancel_newsletter";
	}

	@PostMapping("/cancel_newsletter")
	public String cancelledNewsletter(@RequestParam String email,
									  Model model) {
		try {
			newsletterManager.unsubscribe(email);
		} catch (Exception e) {
			model.addAttribute("errorCancel",true);
		}

		return "cancel_newsletter";
	}

	@PostMapping("register_newsletter")
	public String registerNewsletter(@RequestParam String email,
									 Model model) {
		try {
			newsletterManager.subscribe(email);
			model.addAttribute("successRegister", true);
		} catch (Exception e) {
			model.addAttribute("errorRegister",true);
		}

		return "welcome";
	}

	@PostMapping("/sendEmail")
	public String sendEmail(@RequestParam String text,
							@RequestParam String subject) {

		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "smtp.googlemail.com");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", "465");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.ssl.enable", "false");
		properties.setProperty("mail.smtp.socketFactory.port", "465");
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("bioladen.blattgruen@gmail.com", "blattgruen43");
			}
		});

		try {
			Message msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress("bioladen.blattgruen@gmail.com"));

			msg.addRecipient(Message.RecipientType.BCC, new InternetAddress("bioladen.blattgruen@gmail.com"));

			for (Newsletter nl : newsletterRepository.findBySubscribedTrue()) {
				msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(nl.getMail()));
			}

			msg.setSubject(subject);

			text = text + "<p>Um sich vom Newsletter abzumelden klicken Sie <a href='http://localhost:8080/cancel_newsletter/'>hier</a>.</p>";

			msg.setText(text);
			msg.setContent(text, "text/html");

			Transport.send(msg);
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return "redirect:/admin";
	}

}

