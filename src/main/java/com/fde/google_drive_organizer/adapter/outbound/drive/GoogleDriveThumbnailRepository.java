package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.application.port.outbound.ThumbnailRepository;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Repository
public class GoogleDriveThumbnailRepository implements ThumbnailRepository {

    private static final Logger log = LoggerFactory.getLogger(GoogleDriveThumbnailRepository.class);

    private final ObjectProvider<Drive> driveProvider;
    private final AccessTokenProvider accessTokenProvider;

    public GoogleDriveThumbnailRepository(
            ObjectProvider<Drive> driveProvider,
            AccessTokenProvider accessTokenProvider) {
        this.driveProvider = driveProvider;
        this.accessTokenProvider = accessTokenProvider;
    }

    @Override
    public byte[] getThumbnail(String fileId) {
        try {
            String accessToken = accessTokenProvider.getAccessToken();
            if (accessToken == null) {
                //TODO: throw no access exception
                log.debug("No access token available for fileId: {}", fileId);
                return null;
            }

            File file = driveProvider.getObject().files().get(fileId)
                    .setFields("thumbnailLink")
                    .execute();

            String thumbnailLink = file.getThumbnailLink();
            if (thumbnailLink == null || thumbnailLink.isEmpty()) {
                //TODO: throw not found exception
                log.debug("No thumbnail available for fileId: {}", fileId);
                return null;
            }

            byte[] thumbnailData = downloadThumbnailFromUrl(thumbnailLink, accessToken);
            log.debug("Fetched thumbnail for fileId: {}", fileId);
            return thumbnailData;

        } catch (IOException e) {
            //TODO: throw explicit thumbnail exception
            throw new RuntimeException("Failed to fetch thumbnail from Google Drive for fileId: " + fileId, e);
        }
    }

    private byte[] downloadThumbnailFromUrl(String thumbnailUrl, String accessToken) throws IOException {
        URL url = new URL(thumbnailUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
}
