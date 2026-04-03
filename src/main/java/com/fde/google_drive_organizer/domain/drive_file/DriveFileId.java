package com.fde.google_drive_organizer.domain.drive_file;

public record DriveFileId(String value) {

    public DriveFileId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Drive file id cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
