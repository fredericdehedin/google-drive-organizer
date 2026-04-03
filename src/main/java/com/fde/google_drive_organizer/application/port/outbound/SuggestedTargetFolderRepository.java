package com.fde.google_drive_organizer.application.port.outbound;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileRef;
import com.fde.google_drive_organizer.domain.model.DocumentContent;

public interface SuggestedTargetFolderRepository {

    String suggestTargetFolder(DriveFileRef driveFileRef, DocumentContent content);
}
