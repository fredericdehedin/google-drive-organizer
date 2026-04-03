package com.fde.google_drive_organizer.domain.drive_file.file;

public record DriveMimeType(String value) {

    public DriveMimeType {
        if (value != null && value.isBlank()) {
            throw new IllegalArgumentException("Drive mime type cannot be empty");
        }
    }
}
