package com.snd.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ServerApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

		System.setProperty("RABBITMQ_HOST", dotenv.get("RABBITMQ_HOST"));
		System.setProperty("RABBITMQ_PORT", dotenv.get("RABBITMQ_PORT"));
		System.setProperty("RABBITMQ_USERNAME", dotenv.get("RABBITMQ_USERNAME"));
		System.setProperty("RABBITMQ_PASSWORD", dotenv.get("RABBITMQ_PASSWORD"));

		System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
		System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));

		System.setProperty("ADMIN_GMAIL", dotenv.get("ADMIN_GMAIL"));
		System.setProperty("ADMIN_NAME", dotenv.get("ADMIN_NAME"));
		System.setProperty("ADMIN_PASSWORD", dotenv.get("ADMIN_PASSWORD"));

		System.setProperty("GITHUB_TOKEN", dotenv.get("GITHUB_TOKEN"));
		System.setProperty("REPO_NAME", dotenv.get("REPO_NAME"));
		System.setProperty("BRANCH", dotenv.get("BRANCH"));
		SpringApplication.run(ServerApplication.class, args);
	}

}
