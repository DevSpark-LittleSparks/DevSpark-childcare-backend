package com.devspark.childcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ChildcareApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChildcareApplication.class, args);
	}

}
