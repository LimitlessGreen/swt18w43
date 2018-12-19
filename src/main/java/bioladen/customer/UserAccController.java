package bioladen.customer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.salespointframework.useraccount.AuthenticationManager;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lisa Riedel
 */

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

	/**
	 * gets the password change (from profil)
	 * @param oldPassword
	 * @param newPassword
	 * @param newPasswordAgain
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_MANAGER')||hasRole('ROLE_STAFF')")
	@PostMapping("/profil")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
								 @RequestParam("newPassword") String newPassword,
								 @RequestParam("newPasswordAgain") String newPasswordAgain,
								 Model model) {



		if(StringUtils.isBlank(oldPassword)||StringUtils.isBlank(newPassword)||StringUtils.isBlank(newPasswordAgain)) {
			model.addAttribute("errorPassword", true);
			model.addAttribute("errorPasswordMsg", "Einige Felder wurden nicht ausgefüllt.");

		} else if (newPassword.equals(newPasswordAgain)){
			if (authenticationManager.matches(Password.unencrypted(oldPassword),
					authenticationManager.getCurrentUser().get().getPassword())) {
				if (newPassword.equals(oldPassword)){
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

	/**
	 * resets a password from customer
	 * @param email
	 * @param model
	 * @return
	 */
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
