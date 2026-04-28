package mx.edu.upq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

// No es necesario
@Controller
public class LoginRedirectController {

	@GetMapping("/login")
	public RedirectView redirectToLoginHtml() {
		return new RedirectView("/login.html");
	}

}
