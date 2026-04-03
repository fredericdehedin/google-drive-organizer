package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.application.port.inbound.ExtractDocumentContent;
import com.fde.google_drive_organizer.application.port.inbound.SuggestTargetFolderService;
import com.fde.google_drive_organizer.application.port.outbound.SuggestedTargetFolderRepository;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileRef;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.fde.google_drive_organizer.progress.FileId;
import com.fde.google_drive_organizer.progress.ProgressEventPublisher;
import com.fde.google_drive_organizer.progress.ProgressStep;
import com.fde.google_drive_organizer.progress.TargetFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SuggestTargetFolderUC implements SuggestTargetFolderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuggestTargetFolderUC.class);

    private final ExtractDocumentContent extractDocumentContent;
    private final SuggestedTargetFolderRepository suggestedTargetFolderRepository;
    private final ProgressEventPublisher publisher;

    public SuggestTargetFolderUC(
            ExtractDocumentContent extractDocumentContent,
            SuggestedTargetFolderRepository suggestedTargetFolderRepository,
            ProgressEventPublisher publisher
    ) {
        this.extractDocumentContent = extractDocumentContent;
        this.suggestedTargetFolderRepository = suggestedTargetFolderRepository;
        this.publisher = publisher;
    }

    @Override
    public void suggestTargetFolder(DriveFileRef driveFileRef) {
        FileId fileId = new FileId(driveFileRef.id().value());
        try {
            DocumentContent content = extractDocumentContent.extract(driveFileRef.id().value());
            String suggestedFolder = suggestedTargetFolderRepository.suggestTargetFolder(driveFileRef, content);
            LOGGER.info("Suggested folder for '{}': {}", driveFileRef.name().value(), suggestedFolder);
            publisher.publish(fileId, ProgressStep.DONE, "Archive complete", new TargetFolder(suggestedFolder));
        } catch (Exception e) {
            publisher.publish(fileId, ProgressStep.FAILED, "Archive failed");
            throw e;
        }
    }
}
