package com.fde.google_drive_organizer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
//TODO: move them into test ressources
@TestPropertySource(properties = {
		"drive.root-folder-id=test-folder-id",
		"drive-organizer.ai.base-url=https://api.example.com/v1",
		"drive-organizer.ai.api-key=test-key",
		"drive-organizer.ai.model=gpt-4o"
})
class GoogleDriveOrganizerApplicationTests {

	@Test
	void contextLoads() {
	}

}
