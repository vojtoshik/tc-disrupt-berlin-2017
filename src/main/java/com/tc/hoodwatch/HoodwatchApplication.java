package com.tc.hoodwatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.tc.hoodwatch")
public class HoodwatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoodwatchApplication.class, args);
	}
}
