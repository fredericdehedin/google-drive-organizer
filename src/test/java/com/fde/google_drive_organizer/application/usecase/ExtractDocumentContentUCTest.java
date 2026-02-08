package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.application.port.outbound.DocumentContentRepository;
import com.fde.google_drive_organizer.domain.exception.DocumentContentExtractionException;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtractDocumentContentUCTest {

    @Mock
    private DocumentContentRepository documentContentRepository;

    @InjectMocks
    private ExtractDocumentContentUC extractDocumentContentUC;

    @Test
    void shouldReturnDocumentContentWhenExtractionSucceeds() {
        String fileId = "test-file-id";
        DocumentContent expectedContent = new DocumentContent(fileId, "Extracted text content");
        when(documentContentRepository.extractContent(fileId)).thenReturn(expectedContent);

        DocumentContent result = extractDocumentContentUC.extract(fileId);

        assertThat(result).isEqualTo(expectedContent);
        verify(documentContentRepository).extractContent(fileId);
    }

    @Test
    void shouldThrowExceptionWhenExtractionFails() {
        String fileId = "non-existent-file-id";
        when(documentContentRepository.extractContent(fileId))
                .thenThrow(new DocumentContentExtractionException(fileId, "Failed to extract content"));

        assertThatThrownBy(() -> extractDocumentContentUC.extract(fileId))
                .isInstanceOf(DocumentContentExtractionException.class)
                .hasMessageContaining("Failed to extract content");
        verify(documentContentRepository).extractContent(fileId);
    }
}
