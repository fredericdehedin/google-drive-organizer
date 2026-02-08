package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.application.port.outbound.FileRepository;
import com.fde.google_drive_organizer.domain.model.DriveFile;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleDriveFileRepository implements FileRepository {

    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";

    private final Drive drive;
    private final DriveConfig driveConfig;

    public GoogleDriveFileRepository(
            Drive drive,
            DriveConfig driveConfig) {
        this.drive = drive;
        this.driveConfig = driveConfig;
    }

    @Override
    public List<DriveFile> getFilesInCheckInFolder() {
        try {
            String query = """
                    '%s' in parents and trashed=false and mimeType!='%s'
                    """.formatted(driveConfig.checkInFolderId(), FOLDER_MIME_TYPE).strip();
            
            FileList result = drive.files().list()
                    .setQ(query)
                    .setFields("files(id, name, mimeType, iconLink, thumbnailLink)")
                    .setPageSize(30)
                    .execute();

            if (result.getFiles() == null || result.getFiles().isEmpty()) {
                return Collections.emptyList();
            }

            return result.getFiles().stream()
                    .map(file -> new DriveFile(
                            file.getId(),
                            file.getName(),
                            file.getMimeType(),
                            file.getIconLink(),
                            file.getThumbnailLink()
                    ))
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Failed to list files from Google Drive", e);
        }
    }
}
