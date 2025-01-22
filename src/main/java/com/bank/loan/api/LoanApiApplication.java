package com.bank.loan.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.bank.loan")
@EnableJpaRepositories("com.bank.loan.repository")  // Bu satır eklenmeli
@EntityScan("com.bank.loan.model")
public class LoanApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoanApiApplication.class, args);
	}

}
