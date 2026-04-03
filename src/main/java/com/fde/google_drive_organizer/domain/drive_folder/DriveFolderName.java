package com.fde.google_drive_organizer.domain.drive_folder;

public record DriveFolderName(String value) {

    @Override
    public String toString() {
        return value;
    }
}
