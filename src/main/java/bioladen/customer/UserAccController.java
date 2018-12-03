package bioladen.customer;


import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Password;

import java.util.List;
import java.util.Locale;

@Controller
@Transactional
@RequiredArgsConstructor
public class UserAccController {
	private final UserAccountManager userAccountManager;
	private final AuthenticationManager authenticationManager;

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@RequestMapping("/profil")
	public String account() {

		return "profil";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@PostMapping("/profil")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
								 @RequestParam("newPassword") String newPassword,
								 @RequestParam("newPasswordAgain") String newPasswordAgain,
								 Model model) {



		if(oldPassword.isEmpty()||newPassword.isEmpty()||newPasswordAgain.isEmpty()) {
			model.addAttribute("errorPassword", true);
			model.addAttribute("errorPasswordMsg", "Einige Felder wurden nicht ausgefüllt.");

		} else if (checkEqual(newPassword,newPasswordAgain)){
			if (authenticationManager.matches(Password.unencrypted(oldPassword),
					authenticationManager.getCurrentUser().get().getPassword())) {
				if (checkEqual(newPassword, oldPassword)){
					model.addAttribute("errorPassword", true);
					model.addAttribute("errorPasswordMsg", "Neues Passwort stimmt mit dem alten überein.");

				} else {
					userAccountManager.changePassword(authenticationManager.getCurrentUser().get(), newPassword);
					model.addAttribute("successPassword", true);
					model.addAttribute("successPasswordMsg", "Passwort wurde erfolgreich geändert.");
				}
			}else {
				model.addAttribute("errorPassword", true);
				model.addAttribute("errorPasswordMsg", "Altes Passwort ist inkorrekt.");
			}
		} else {
			model.addAttribute("errorPassword", true);
			model.addAttribute("errorPasswordMsg", "Die neuen Passwörter stimmen nicht überein.");
		}
		return "profil";
	}

	public boolean checkEqual(String firstPassword, String secondPassword){
		return firstPassword.equals(secondPassword);
	}

	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/customerlist/resetPassword")
	String resetPassword(@RequestParam String email, Model model) {

		if (authenticationManager.matches(Password.unencrypted("blattgrün43"),
				userAccountManager.findByUsername(email).get().getPassword())){
			model.addAttribute("errorPasswordReset", true);
			model.addAttribute("errorPasswordResetMsg", "Passwort ist bereits das Standardpassword");

		} else {
			userAccountManager.changePassword(authenticationManager.getCurrentUser().get(), "blattgrün43");
			model.addAttribute("successPasswordReset", true);
		}

		return "redirect:/customerlist";
	}
}