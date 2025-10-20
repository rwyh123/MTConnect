package com.example.MTConnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MtConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MtConnectApplication.class, args);
	}

}
