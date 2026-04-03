package com.fde.google_drive_organizer.domain.drive_file;

public record DriveMimeType(String value) {

    public DriveMimeType {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Drive mime type cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
