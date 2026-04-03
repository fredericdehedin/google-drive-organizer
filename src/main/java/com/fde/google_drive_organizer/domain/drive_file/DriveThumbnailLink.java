package com.fde.google_drive_organizer.domain.drive_file;

public record DriveThumbnailLink(String value) {

    public DriveThumbnailLink {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Drive thumbnail link cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
