package com.fde.google_drive_organizer.domain.drive_file.document_content;

import java.util.Objects;

public record DriveFileDocumentContentText(String value) {

    public DriveFileDocumentContentText {
        Objects.requireNonNull(value, "value must not be null");
    }
}
