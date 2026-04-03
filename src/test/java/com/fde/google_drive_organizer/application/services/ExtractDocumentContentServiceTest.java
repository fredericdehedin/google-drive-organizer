package com.fde.google_drive_organizer.application.services;

import com.fde.google_drive_organizer.application.port.outbound.DocumentContentRepository;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_file.document_content.DocumentContentExtractionException;
import com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContent;
import com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContentTestFixture;
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
class ExtractDocumentContentServiceTest {

    @Mock
    private DocumentContentRepository documentContentRepository;

    @InjectMocks
    private ExtractDocumentContentService extractDocumentContentService;

    @Test
    void shouldReturnDocumentContentWhenExtractionSucceeds() {
        DriveFileId fileId = new DriveFileId("test-file-id");
        DriveFileDocumentContent expectedContent = DriveFileDocumentContentTestFixture.aDriveFileDocumentContent()
                .withFileId(fileId.value())
                .withTextContent("Extracted text content")
                .build();
        when(documentContentRepository.extractContent(fileId)).thenReturn(expectedContent);

        DriveFileDocumentContent result = extractDocumentContentService.extract(fileId);

        assertThat(result).isEqualTo(expectedContent);
        verify(documentContentRepository).extractContent(fileId);
    }

    @Test
    void shouldThrowExceptionWhenExtractionFails() {
        DriveFileId fileId = new DriveFileId("non-existent-file-id");
        when(documentContentRepository.extractContent(fileId))
                .thenThrow(new DocumentContentExtractionException(fileId.value(), "Failed to extract content"));

        assertThatThrownBy(() -> extractDocumentContentService.extract(fileId))
                .isInstanceOf(DocumentContentExtractionException.class)
                .hasMessageContaining("Failed to extract content");
        verify(documentContentRepository).extractContent(fileId);
    }
}
