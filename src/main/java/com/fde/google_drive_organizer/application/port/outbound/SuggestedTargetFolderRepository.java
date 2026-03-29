package com.fde.google_drive_organizer.application.port.outbound;

import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.fde.google_drive_organizer.domain.model.DriveFile;

public interface SuggestedTargetFolderRepository {

    String suggestTargetFolder(DriveFile driveFile, DocumentContent content);
}
