package com.library.libraryAutomation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//pysu smxj mxia shvb
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableAsync
public class LibraryAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryAutomationApplication.class, args);
	}

    @Bean //Autowired kullanmak icin yazilir
    public PasswordEncoder passwordEncoder() { //PasswordEncoder , sonra heshing algoritmasini degismek istedigimizde burda degisecez heryerde yok
        return new BCryptPasswordEncoder(); //heslemek algoritmasi , en populer
    }
}
