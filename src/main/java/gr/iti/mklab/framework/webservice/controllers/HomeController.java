package gr.iti.mklab.framework.webservice.controllers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Simple little @Controller that invokes Facebook and renders the result.
 * The injected {@link Facebook} reference is configured with the required authorization credentials for the current user behind the scenes.
 * @author Keith Donald
 */
@Controller
public class HomeController {

	private final Facebook facebook;
	
	@Inject
	public HomeController(Facebook facebook) {
		this.facebook = facebook;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		List<Reference> friends = facebook.friendOperations().getFriends();
		model.addAttribute("friends", friends);
		return "home";
	}

}