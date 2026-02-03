package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.domain.model.DriveFile;
import com.fde.google_drive_organizer.domain.port.outbound.FileRepository;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleDriveFileRepository implements FileRepository {

    private static final String APPLICATION_NAME = "Google Drive Organizer";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final AccessTokenProvider accessTokenProvider;
    private final String checkInFolderId;

    public GoogleDriveFileRepository(
            AccessTokenProvider accessTokenProvider,
            @Value("${drive.check-in-folder-id}") String checkInFolderId) {
        this.accessTokenProvider = accessTokenProvider;
        this.checkInFolderId = checkInFolderId;
    }

    @Override
    public List<DriveFile> getFilesInCheckInFolder() {
        String accessToken = accessTokenProvider.getAccessToken();
        
        if (accessToken == null) {
            throw new IllegalStateException("No access token available");
        }

        try {
            Drive driveService = buildDriveService(accessToken);
            
            FileList result = driveService.files().list()
                    .setQ("'" + checkInFolderId + "' in parents and trashed=false")
                    .setFields("files(id, name, mimeType, iconLink, thumbnailLink)")
                    .setPageSize(10)
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

        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to list files from Google Drive", e);
        }
    }

    private Drive buildDriveService(String accessToken) throws GeneralSecurityException, IOException {
        HttpRequestInitializer requestInitializer = request -> {
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        };

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
