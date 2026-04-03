package com.fde.google_drive_organizer.domain.drive_file;

public record DriveFile(
        DriveFileId id,
        DriveFileName name,
        DriveMimeType mimeType,
        DriveIconLink iconLink,
        DriveThumbnailLink thumbnailLink
) {

    public DriveFile {
        if (id == null) {
            throw new IllegalArgumentException("Drive file id cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Drive file name cannot be null");
        }
        // mimeType, iconLink, thumbnailLink may be null when only a reference exists
    }
}

