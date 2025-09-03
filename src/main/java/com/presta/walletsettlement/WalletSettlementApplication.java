package com.presta.walletsettlement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class WalletSettlementApplication {
	public static void main(String[] args) {
		SpringApplication.run(WalletSettlementApplication.class, args);
	}
}
