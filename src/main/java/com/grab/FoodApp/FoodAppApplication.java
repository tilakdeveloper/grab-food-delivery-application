package com.grab.FoodApp;

import com.grab.FoodApp.email_notification.dtos.NotificationDTO;
import com.grab.FoodApp.email_notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
//@RequiredArgsConstructor
public class FoodAppApplication {

//    private final NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(FoodAppApplication.class, args);
	}

//    @Bean
//    CommandLineRunner runner() {
//        return args -> {
//            NotificationDTO notificationDTO = NotificationDTO.builder()
//                    .recipient("")
//                    .subject("Test Email from FoodApp")
//                    .body("<h1>This is a test email from FoodApp</h1>")
//                    .isHtml(true)
//                    .build();
//            notificationService.sendEmail(notificationDTO);
//        };
//    }

}
