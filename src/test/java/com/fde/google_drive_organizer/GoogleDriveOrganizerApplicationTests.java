package com.fde.google_drive_organizer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"drive.root-folder-id=test-folder-id"
})
class GoogleDriveOrganizerApplicationTests {

	@Test
	void contextLoads() {
	}

}
