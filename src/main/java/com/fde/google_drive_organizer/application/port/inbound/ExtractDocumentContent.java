package com.fde.google_drive_organizer.application.port.inbound;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContent;

@FunctionalInterface
public interface ExtractDocumentContent {
    DriveFileDocumentContent extract(DriveFileId fileId);
}
