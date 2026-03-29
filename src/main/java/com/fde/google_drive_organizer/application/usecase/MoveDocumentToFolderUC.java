package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.application.port.inbound.ExtractDocumentContent;
import com.fde.google_drive_organizer.application.port.inbound.MoveDocumentToFolder;
import com.fde.google_drive_organizer.application.port.outbound.SuggestedTargetFolderRepository;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.fde.google_drive_organizer.domain.model.DriveFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MoveDocumentToFolderUC implements MoveDocumentToFolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveDocumentToFolderUC.class);

    private final ExtractDocumentContent extractDocumentContent;
    private final SuggestedTargetFolderRepository suggestedTargetFolderRepository;

    public MoveDocumentToFolderUC(
            ExtractDocumentContent extractDocumentContent,
            SuggestedTargetFolderRepository suggestedTargetFolderRepository
    ) {
        this.extractDocumentContent = extractDocumentContent;
        this.suggestedTargetFolderRepository = suggestedTargetFolderRepository;
    }

    @Override
    public void move(DriveFile driveFile) {
        DocumentContent content = extractDocumentContent.extract(driveFile.id());
        String suggestedFolder = suggestedTargetFolderRepository.suggestTargetFolder(driveFile, content);
        LOGGER.info("Suggested folder for '{}': {}", driveFile.name(), suggestedFolder);
    }
}
