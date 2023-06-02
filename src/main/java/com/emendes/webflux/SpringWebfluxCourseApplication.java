package com.emendes.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@SpringBootApplication
public class SpringWebfluxCourseApplication {

	public static void main(String[] args) {
		System.out.println(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("1234567890"));
		SpringApplication.run(SpringWebfluxCourseApplication.class, args);
	}

}
