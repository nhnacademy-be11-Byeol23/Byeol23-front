package com.nhnacademy.byeol23front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	@GetMapping("/")
	public String mainPage() {
		return "index";
	}

	@GetMapping("/error")
	public String showError() {
		return "error";
	}

}
