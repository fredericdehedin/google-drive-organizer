package com.fde.google_drive_organizer.domain.drive_file;

public record DriveIconLink(String value) {

    public DriveIconLink {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Drive icon link cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
