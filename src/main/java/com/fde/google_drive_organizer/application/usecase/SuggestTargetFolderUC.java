package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.application.port.inbound.ExtractDocumentContent;
import com.fde.google_drive_organizer.application.port.inbound.SuggestTargetFolderService;
import com.fde.google_drive_organizer.application.port.outbound.SuggestedTargetFolderRepository;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContent;
import com.fde.google_drive_organizer.domain.drive_file.ref.DriveFileRef;
import com.fde.google_drive_organizer.domain.drive_folder.DriveFolderId;
import com.fde.google_drive_organizer.domain.drive_folder.DriveFolderName;
import com.fde.google_drive_organizer.domain.drive_folder.DriveTargetFolder;
import com.fde.google_drive_organizer.domain.suggest_target_folder_progress.ProgressStep;
import com.fde.google_drive_organizer.domain.suggest_target_folder_progress.SuggestTargetFolderProgressPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SuggestTargetFolderUC implements SuggestTargetFolderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuggestTargetFolderUC.class);

    private final ExtractDocumentContent extractDocumentContent;
    private final SuggestedTargetFolderRepository suggestedTargetFolderRepository;
    private final SuggestTargetFolderProgressPublisher publisher;

    public SuggestTargetFolderUC(
            ExtractDocumentContent extractDocumentContent,
            SuggestedTargetFolderRepository suggestedTargetFolderRepository,
            SuggestTargetFolderProgressPublisher publisher
    ) {
        this.extractDocumentContent = extractDocumentContent;
        this.suggestedTargetFolderRepository = suggestedTargetFolderRepository;
        this.publisher = publisher;
    }

    @Override
    public void suggestTargetFolder(DriveFileRef driveFileRef) {
        DriveFileId driveFileId = driveFileRef.id();
        try {
            DriveFileDocumentContent content = extractDocumentContent.extract(driveFileId);
            String suggestedFolder = suggestedTargetFolderRepository.suggestTargetFolder(driveFileRef, content);
            LOGGER.info("Suggested folder for '{}': {}", driveFileRef.name().value(), suggestedFolder);
            publisher.publish(driveFileId, ProgressStep.DONE, "Archive complete", new DriveTargetFolder(new DriveFolderId(null), new DriveFolderName(suggestedFolder)));
        } catch (Exception e) {
            publisher.publish(driveFileId, ProgressStep.FAILED, "Archive failed");
            throw e;
        }
    }
}
