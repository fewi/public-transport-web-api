package de.fewi.ptwa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PublicTransportWebApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublicTransportWebApiApplication.class, args);
	}
}
