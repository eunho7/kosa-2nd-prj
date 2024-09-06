package com.example._team;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Application {	//	// //

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		System.out.println("       /\\_/\\  ");
		System.out.println("      ( o.o )  ");
		System.out.println("       > ^ <  ");
		System.out.println("      /\\_^_/\\ ");
		System.out.println("SERVER START...");
	}

}
