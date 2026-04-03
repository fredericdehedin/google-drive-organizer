package com.fde.google_drive_organizer.domain.drive_file.file;

public record DriveIconLink(String value) {

    public DriveIconLink {
        if (value != null && value.isBlank()) {
            throw new IllegalArgumentException("Drive icon link cannot be empty");
        }
    }
}
