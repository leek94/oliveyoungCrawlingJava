package com.example.cosmeticCrawlingJava;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBatchProcessing // 배치 기능 활성화
@SpringBootApplication
public class CosmeticCrawlingJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CosmeticCrawlingJavaApplication.class, args);
	}

}
