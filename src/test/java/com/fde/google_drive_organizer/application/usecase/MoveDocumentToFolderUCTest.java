package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.application.port.inbound.ExtractDocumentContent;
import com.fde.google_drive_organizer.application.port.outbound.SuggestedTargetFolderRepository;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileTestFixture;
import com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContent;
import com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContentTestFixture;
import com.fde.google_drive_organizer.domain.drive_file.ref.DriveFileRef;
import com.fde.google_drive_organizer.domain.drive_folder.DriveTargetFolderTestFixture;
import com.fde.google_drive_organizer.domain.suggest_target_folder_progress.ProgressStep;
import com.fde.google_drive_organizer.domain.suggest_target_folder_progress.SuggestTargetFolderProgressPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuggestTargetFolderUCTest {

    @Mock
    private ExtractDocumentContent extractDocumentContent;

    @Mock
    private SuggestedTargetFolderRepository suggestedTargetFolderRepository;

    @Mock
    private SuggestTargetFolderProgressPublisher publisher;

    @InjectMocks
    private SuggestTargetFolderUC suggestTargetFolderUC;

    @Test
    void shouldExtractContentAndPrintSuggestedFolder() {
        DriveFileRef driveFileRef = DriveFileTestFixture.aDriveFileRef()
                .withId("file-123")
                .withName("salary-certificate-2025.pdf")
                .build();
        DriveFileDocumentContent content = DriveFileDocumentContentTestFixture.aDriveFileDocumentContent()
                .withFileId("file-123")
                .withTextContent("Salary certificate text")
                .build();
        when(extractDocumentContent.extract(driveFileRef.id())).thenReturn(content);
        when(suggestedTargetFolderRepository.suggestTargetFolder(driveFileRef, content)).thenReturn("Taxes/2025/02_Income");

        suggestTargetFolderUC.suggestTargetFolder(driveFileRef);

        verify(extractDocumentContent).extract(driveFileRef.id());
        verify(suggestedTargetFolderRepository).suggestTargetFolder(driveFileRef, content);
        verify(publisher).publish(driveFileRef.id(), ProgressStep.DONE, "Archive complete", DriveTargetFolderTestFixture.aDriveTargetFolder()
                .withName("Taxes/2025/02_Income")
                .build());
    }
}
