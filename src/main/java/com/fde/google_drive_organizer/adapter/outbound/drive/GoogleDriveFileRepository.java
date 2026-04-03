package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.application.port.outbound.FileRepository;
import com.fde.google_drive_organizer.domain.drive_file.DriveFile;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileName;
import com.fde.google_drive_organizer.domain.drive_file.DriveIconLink;
import com.fde.google_drive_organizer.domain.drive_file.DriveMimeType;
import com.fde.google_drive_organizer.domain.drive_file.DriveThumbnailLink;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleDriveFileRepository implements FileRepository {

    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";

    private final ObjectProvider<Drive> driveProvider;
    private final DriveConfig driveConfig;

    public GoogleDriveFileRepository(
            ObjectProvider<Drive> driveProvider,
            DriveConfig driveConfig) {
        this.driveProvider = driveProvider;
        this.driveConfig = driveConfig;
    }

    @Override
    public List<DriveFile> getFilesInRootFolder() {
        try {
            String query = """
                    '%s' in parents and trashed=false and mimeType!='%s'
                    """.formatted(driveConfig.rootFolderId(), FOLDER_MIME_TYPE).strip();

            FileList result = driveProvider.getObject().files().list()
                    .setQ(query)
                    .setFields("files(id, name, mimeType, iconLink, thumbnailLink)")
                    .setPageSize(30)
                    .execute();

            if (result.getFiles() == null || result.getFiles().isEmpty()) {
                return Collections.emptyList();
            }

            return result.getFiles().stream()
                    .map(file -> new DriveFile(
                            new DriveFileId(file.getId()),
                            new DriveFileName(file.getName()),
                            new DriveMimeType(file.getMimeType()),
                            file.getIconLink() == null ? null : new DriveIconLink(file.getIconLink()),
                            file.getThumbnailLink() == null ? null : new DriveThumbnailLink(file.getThumbnailLink())
                    ))
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Failed to list files from Google Drive", e);
        }
    }
}
