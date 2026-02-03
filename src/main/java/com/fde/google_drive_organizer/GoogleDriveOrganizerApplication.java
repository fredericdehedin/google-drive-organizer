package com.fde.google_drive_organizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan(basePackages = {
		"com.fde.google_drive_organizer"
})
@SpringBootApplication
public class GoogleDriveOrganizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoogleDriveOrganizerApplication.class, args);
	}

}
