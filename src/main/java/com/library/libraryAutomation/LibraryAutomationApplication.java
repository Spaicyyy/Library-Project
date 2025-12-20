package com.library.libraryAutomation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//pysu smxj mxia shvb
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})//Springboot'un normal guvenliyini burakib import etdiyimizi kullaniyor
@EnableAsync //Email islemleri daha hizli calisiyor
@Configuration // Bean , Component ... , projede bunlar oldugunu springboota gosteriyor
@EnableWebSecurity //Web guvenliyini kullanacagini springboota soyluyor
public class LibraryAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryAutomationApplication.class, args);
	}

}
