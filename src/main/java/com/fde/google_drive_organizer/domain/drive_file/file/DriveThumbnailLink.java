package com.fde.google_drive_organizer.domain.drive_file.file;

public record DriveThumbnailLink(String value) {

    public DriveThumbnailLink {
        if (value != null && value.isBlank()) {
            throw new IllegalArgumentException("Drive thumbnail link cannot be empty");
        }
    }
}
