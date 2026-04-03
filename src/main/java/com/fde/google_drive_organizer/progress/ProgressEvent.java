package com.fde.google_drive_organizer.progress;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_folder.DriveTargetFolder;

import java.time.Instant;

public record ProgressEvent(
        DriveFileId fileId,
        ProgressStep step,
        String message,
        DriveTargetFolder targetFolder,
        Instant timestamp
) {}
