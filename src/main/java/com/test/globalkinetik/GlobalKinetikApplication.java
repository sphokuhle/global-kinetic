package com.test.globalkinetik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author S'phokuhle on 9/13/2021
 */
@SpringBootApplication
@EnableScheduling
public class GlobalKinetikApplication {
	public static void main(String[] args) {
		SpringApplication.run(GlobalKinetikApplication.class, args);
	}
}
