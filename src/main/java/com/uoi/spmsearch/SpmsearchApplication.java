package com.uoi.spmsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.gcp.data.firestore.repository.config.EnableReactiveFirestoreRepositories;

@SpringBootApplication
@EnableCaching
@EnableReactiveFirestoreRepositories
public class SpmsearchApplication {


	public static void main(String[] args) {
		SpringApplication.run(SpmsearchApplication.class, args);
	}
}
