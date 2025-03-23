package com.e_val.e_Val;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.e_val.e_Val.model") // Scan entity package
@EnableJpaRepositories("com.e_val.e_Val.repository") // Scan repository package
public class EValApplication {

	public static void main(String[] args) {
		SpringApplication.run(EValApplication.class, args);
	}

}
