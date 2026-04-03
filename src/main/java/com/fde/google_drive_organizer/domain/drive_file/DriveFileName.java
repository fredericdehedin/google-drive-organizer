package com.fde.google_drive_organizer.domain.drive_file;

public record DriveFileName(String value) {

    public DriveFileName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Drive file name cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
