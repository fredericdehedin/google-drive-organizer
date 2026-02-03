package com.fde.google_drive_organizer.domain.model;

import org.junit.jupiter.api.Test;

import static com.fde.google_drive_organizer.domain.model.DriveFileTestFixture.aDriveFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DriveFileTest {

    @Test
    void shouldCreateDriveFileWithAllFields() {
        DriveFile driveFile = aDriveFile()
                .withId("file-123")
                .withName("document.pdf")
                .withMimeType("application/pdf")
                .withIconLink("https://drive-thirdparty.googleusercontent.com/16/type/application/pdf")
                .withThumbnailLink("https://lh3.googleusercontent.com/thumbnail")
                .build();

        assertThat(driveFile.id()).isEqualTo("file-123");
        assertThat(driveFile.name()).isEqualTo("document.pdf");
        assertThat(driveFile.mimeType()).isEqualTo("application/pdf");
        assertThat(driveFile.iconLink()).isEqualTo("https://drive-thirdparty.googleusercontent.com/16/type/application/pdf");
        assertThat(driveFile.thumbnailLink()).isEqualTo("https://lh3.googleusercontent.com/thumbnail");
    }

    @Test
    void shouldCreateDriveFileWithNullThumbnailLink() {
        DriveFile driveFile = aDriveFile()
                .withId("file-456")
                .withName("spreadsheet.xlsx")
                .withMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .withIconLink("https://drive-thirdparty.googleusercontent.com/16/type/application/vnd.ms-excel")
                .withThumbnailLink(null)
                .build();

        assertThat(driveFile.id()).isEqualTo("file-456");
        assertThat(driveFile.name()).isEqualTo("spreadsheet.xlsx");
        assertThat(driveFile.thumbnailLink()).isNull();
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> aDriveFile().withId(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File id cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenIdIsBlank() {
        assertThatThrownBy(() -> aDriveFile().withId("  ").build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File id cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThatThrownBy(() -> aDriveFile().withName(null).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File name cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThatThrownBy(() -> aDriveFile().withName("  ").build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File name cannot be null or empty");
    }

    @Test
    void shouldAllowNullMimeType() {
        DriveFile driveFile = aDriveFile()
                .withMimeType(null)
                .build();

        assertThat(driveFile.mimeType()).isNull();
    }

    @Test
    void shouldAllowNullIconLink() {
        DriveFile driveFile = aDriveFile()
                .withIconLink(null)
                .build();

        assertThat(driveFile.iconLink()).isNull();
    }
}
