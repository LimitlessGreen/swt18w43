package bioladen.newsletter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
	private static NewsletterRepository newsletterRepository;

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
		model.addAttribute("errorCancel",true);
		//TODO cancel Newsletter

		return "cancel_newsletter";
	}

	@PostMapping("register_newsletter")
	public String registerNewsletter(@RequestParam String email,
									 Model model) {
		model.addAttribute("errorRegister",true);
		//TODO register Newsletter
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

			//for (Newsletter nl : newsletterRepository.findAll()) {
			//	msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(nl.getMail()));
			//}

			msg.addRecipient(Message.RecipientType.BCC, new InternetAddress("info@akulisch.de"));
			msg.setSubject(subject);

			msg.setText(text);

			Transport.send(msg);
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return "redirect:/admin";
	}

}

