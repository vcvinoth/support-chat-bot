package com.example.supportchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SupportChatBotBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupportChatBotBackendApplication.class, args);
	}

}
