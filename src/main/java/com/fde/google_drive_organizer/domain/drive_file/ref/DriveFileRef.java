package com.fde.google_drive_organizer.domain.drive_file.ref;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileName;

public record DriveFileRef(
        DriveFileId id,
        DriveFileName name
) {

    public DriveFileRef {
        if (id == null) {
            throw new IllegalArgumentException("Drive file id cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Drive file name cannot be null");
        }
    }
}
