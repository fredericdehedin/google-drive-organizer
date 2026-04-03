package com.fde.google_drive_organizer.domain.drive_file.file;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileName;
import org.jspecify.annotations.NonNull;

public record DriveFile(
        @NonNull DriveFileId id,
        @NonNull DriveFileName name,
        @NonNull DriveMimeType mimeType,
        @NonNull DriveIconLink iconLink,
        @NonNull DriveThumbnailLink thumbnailLink
) {
}

