package com.pikachugo.RestManager;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.pikachugo.RestManager.connector")
@SpringBootApplication
public class RestManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestManagerApplication.class, args);
	}

}
