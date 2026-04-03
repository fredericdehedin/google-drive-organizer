package com.fde.google_drive_organizer.domain.drive_file.document_content;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import org.junit.jupiter.api.Test;

import static com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContentTestFixture.aDriveFileDocumentContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void shouldThrowExceptionWhenFileIdIsNull() {
        assertThatThrownBy(() -> new DriveFileDocumentContent(null, new DriveFileDocumentContentText("text")))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("fileId must not be null");
    }

    @Test
    void shouldThrowExceptionWhenFileIdIsBlank() {
        assertThatThrownBy(() -> new DriveFileDocumentContent(new DriveFileId("  "), new DriveFileDocumentContentText("text")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Drive file id cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenTextContentIsNull() {
        assertThatThrownBy(() -> new DriveFileDocumentContent(new DriveFileId("file-123"), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("textContent must not be null");
    }
}
