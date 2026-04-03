package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.application.port.inbound.ExtractDocumentContent;
import com.fde.google_drive_organizer.application.port.outbound.SuggestedTargetFolderRepository;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.fde.google_drive_organizer.domain.model.DocumentContentTestFixture;
import com.fde.google_drive_organizer.domain.model.DriveFile;
import com.fde.google_drive_organizer.domain.model.DriveFileTestFixture;
import com.fde.google_drive_organizer.progress.ProgressEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoveDocumentToFolderUCTest {

    @Mock
    private ExtractDocumentContent extractDocumentContent;

    @Mock
    private SuggestedTargetFolderRepository suggestedTargetFolderRepository;

    @Mock
    private ProgressEventPublisher publisher;

    @InjectMocks
    private MoveDocumentToFolderUC moveDocumentToFolderUC;

    @Test
    void shouldExtractContentAndPrintSuggestedFolder() {
        DriveFile driveFile = DriveFileTestFixture.aDriveFile()
                .withId("file-123")
                .withName("salary-certificate-2025.pdf")
                .build();
        DocumentContent content = DocumentContentTestFixture.aDocumentContent()
                .withFileId("file-123")
                .withTextContent("Salary certificate text")
                .build();
        when(extractDocumentContent.extract("file-123")).thenReturn(content);
        when(suggestedTargetFolderRepository.suggestTargetFolder(driveFile, content)).thenReturn("Taxes/2025/02_Income");

        moveDocumentToFolderUC.move(driveFile);

        verify(extractDocumentContent).extract("file-123");
        verify(suggestedTargetFolderRepository).suggestTargetFolder(driveFile, content);
    }
}
