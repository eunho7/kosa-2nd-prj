package com.example._team.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {
	  @GetMapping("/health")
	    public String healthCheck() {
	        return "Application is running!";
	    }
}
