package com.smartparking.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SmartParkingManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartParkingManagementApplication.class, args);
	}

}
