package com.fde.google_drive_organizer.domain.drive_file;

import com.fde.google_drive_organizer.domain.drive_file.file.DriveFile;
import org.junit.jupiter.api.Test;

import static com.fde.google_drive_organizer.domain.drive_file.DriveFileTestFixture.aDriveFile;
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

        assertThat(driveFile.id().value()).isEqualTo("file-123");
        assertThat(driveFile.name().value()).isEqualTo("document.pdf");
        assertThat(driveFile.mimeType().value()).isEqualTo("application/pdf");
        assertThat(driveFile.iconLink().value()).isEqualTo("https://drive-thirdparty.googleusercontent.com/16/type/application/pdf");
        assertThat(driveFile.thumbnailLink().value()).isEqualTo("https://lh3.googleusercontent.com/thumbnail");
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

        assertThat(driveFile.id().value()).isEqualTo("file-456");
        assertThat(driveFile.name().value()).isEqualTo("spreadsheet.xlsx");
        assertThat(driveFile.thumbnailLink().value()).isNull();
    }

    @Test
    void shouldAllowNullMimeType() {
        DriveFile driveFile = aDriveFile()
                .withMimeType(null)
                .build();

        assertThat(driveFile.mimeType().value()).isNull();
    }

    @Test
    void shouldAllowNullIconLink() {
        DriveFile driveFile = aDriveFile()
                .withIconLink(null)
                .build();

        assertThat(driveFile.iconLink().value()).isNull();
    }
}
