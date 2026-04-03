package com.fde.google_drive_organizer.domain.drive_file.document_content;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;

import java.util.Objects;

public record DriveFileDocumentContent(
        DriveFileId fileId,
        DriveFileDocumentContentText textContent
) {
    public DriveFileDocumentContent {
        Objects.requireNonNull(fileId, "fileId must not be null");
        Objects.requireNonNull(textContent, "textContent must not be null");
    }
}
