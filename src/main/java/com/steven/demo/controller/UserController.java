package com.steven.demo.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class UserController {
	
	@PostMapping("/login")
	public String login() {
	return "login";
	}
	
	@GetMapping("/")
	public String index() {
	return "hello";
	}
	
	@GetMapping("/userpage")
	public String httpApi() {
	System.out.println("success");
	System.out.println("return result: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
	return "userpage";
	}
	
	@GetMapping("/adminpage")
	public String httpSuite() {
	return "userpage";
	}

}
