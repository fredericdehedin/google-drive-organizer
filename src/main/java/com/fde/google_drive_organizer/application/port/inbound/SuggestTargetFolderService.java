package com.fde.google_drive_organizer.application.port.inbound;

import com.fde.google_drive_organizer.domain.drive_file.ref.DriveFileRef;

@FunctionalInterface
public interface SuggestTargetFolderService {

    void suggestTargetFolder(DriveFileRef driveFileRef);
}
