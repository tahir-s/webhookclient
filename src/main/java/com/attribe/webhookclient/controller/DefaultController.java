package com.attribe.webhookclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class DefaultController {	
	@GetMapping()
	public String get() {
		System.out.println("Trst.....");
		return "This is WebHook API ...";
	}
}
