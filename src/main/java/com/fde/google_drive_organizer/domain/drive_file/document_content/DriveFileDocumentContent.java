package com.fde.google_drive_organizer.domain.drive_file.document_content;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import org.jspecify.annotations.NonNull;

public record DriveFileDocumentContent(
        @NonNull DriveFileId fileId,
        @NonNull DriveFileDocumentContentText textContent
) {
}
