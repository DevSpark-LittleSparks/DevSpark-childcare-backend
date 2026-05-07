package com.devspark.childcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@org.springframework.scheduling.annotation.EnableScheduling
public class ChildcareApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChildcareApplication.class, args);
	}

}
