package com.fde.google_drive_organizer.application.port.outbound;
 
import com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContent;
import com.fde.google_drive_organizer.domain.drive_file.ref.DriveFileRef;
 
public interface SuggestedTargetFolderRepository {
 
    String suggestTargetFolder(DriveFileRef driveFileRef, DriveFileDocumentContent content);
}
