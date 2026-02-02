package com.fde.google_drive_organizer.domain.model;

public record DriveFile(String id, String name) {
    
    public DriveFile {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("File id cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
    }
}
