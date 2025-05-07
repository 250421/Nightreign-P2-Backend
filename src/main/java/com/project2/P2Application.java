package com.project2;

import com.project2.resources.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class P2Application {

	public static void main(String[] args) {
		EnvLoader.init();
		SpringApplication.run(P2Application.class, args);
	}

}
