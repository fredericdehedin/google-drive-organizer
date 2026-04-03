package com.fde.google_drive_organizer.domain.drive_file.document_content;

import org.junit.jupiter.api.Test;

import static com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContentTestFixture.aDriveFileDocumentContent;
import static org.assertj.core.api.Assertions.assertThat;

class DriveFileDocumentContentTest {

    @Test
    void shouldCreateDriveFileDocumentContentWithValidData() {
        DriveFileDocumentContent documentContent = aDriveFileDocumentContent()
                .withFileId("file-123")
                .withTextContent("Sample text content")
                .build();

        assertThat(documentContent.fileId().value()).isEqualTo("file-123");
        assertThat(documentContent.textContent().value()).isEqualTo("Sample text content");
    }

    @Test
    void shouldCreateDriveFileDocumentContentWithEmptyTextContent() {
        DriveFileDocumentContent documentContent = aDriveFileDocumentContent()
                .withFileId("file-123")
                .withTextContent("")
                .build();

        assertThat(documentContent.fileId().value()).isEqualTo("file-123");
        assertThat(documentContent.textContent().value()).isEmpty();
    }

}
