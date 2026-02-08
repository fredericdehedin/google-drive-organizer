package com.fde.google_drive_organizer.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentContentTest {

    @Test
    void shouldCreateDocumentContentWithValidData() {
        DocumentContent documentContent = new DocumentContent("file-123", "Sample text content");

        assertThat(documentContent.fileId()).isEqualTo("file-123");
        assertThat(documentContent.textContent()).isEqualTo("Sample text content");
    }

    @Test
    void shouldCreateDocumentContentWithEmptyTextContent() {
        DocumentContent documentContent = new DocumentContent("file-123", "");

        assertThat(documentContent.fileId()).isEqualTo("file-123");
        assertThat(documentContent.textContent()).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenFileIdIsNull() {
        assertThatThrownBy(() -> new DocumentContent(null, "text"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("fileId must not be null");
    }

    @Test
    void shouldThrowExceptionWhenFileIdIsBlank() {
        assertThatThrownBy(() -> new DocumentContent("  ", "text"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("fileId must not be blank");
    }

    @Test
    void shouldThrowExceptionWhenTextContentIsNull() {
        assertThatThrownBy(() -> new DocumentContent("file-123", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("textContent must not be null");
    }
}
