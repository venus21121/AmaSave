package com.example.retailpricetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableAsync // Enables async processing
@EnableScheduling
public class AmaSaveApplication {

	//entry point
	public static void main(String[] args) {
		SpringApplication.run(AmaSaveApplication.class, args);
	}
	@GetMapping("/hello") //endpoint
	public String sayHello(@RequestParam(value = "myName", defaultValue = "Venus") String name) {
		return String.format("Hello %s!", name);
	}


}
