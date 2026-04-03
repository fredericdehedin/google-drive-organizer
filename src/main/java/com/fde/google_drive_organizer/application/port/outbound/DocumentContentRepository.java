package com.fde.google_drive_organizer.application.port.outbound;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContent;

public interface DocumentContentRepository {

    DriveFileDocumentContent extractContent(DriveFileId fileId);
}
