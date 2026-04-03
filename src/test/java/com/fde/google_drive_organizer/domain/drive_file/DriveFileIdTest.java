package com.fde.google_drive_organizer.domain.drive_file;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DriveFileIdTest {

    @Test
    void shouldThrowExceptionWhenFileIdIsBlank() {
        assertThatThrownBy(() -> new DriveFileId("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Drive file id cannot be null or empty");
    }
}
