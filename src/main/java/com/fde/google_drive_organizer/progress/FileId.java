package com.fde.google_drive_organizer.progress;

public record FileId(String value) {
    public FileId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("FileId value cannot be null or blank");
        }
    }
}
