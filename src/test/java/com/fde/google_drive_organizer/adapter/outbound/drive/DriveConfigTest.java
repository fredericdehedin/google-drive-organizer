package com.fde.google_drive_organizer.adapter.outbound.drive;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DriveConfigTest.TestConfig.class)
@TestPropertySource(properties = {
        "drive.check-in-folder-id=test-folder-123"
})
class DriveConfigTest {

    @EnableConfigurationProperties(DriveConfig.class)
    static class TestConfig {}

    @Autowired
    private DriveConfig config;

    @Test
    void shouldBindCheckInFolderIdProperty() {
        assertThat(config.checkInFolderId()).isEqualTo("test-folder-123");
    }
}
