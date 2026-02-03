package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.adapter.outbound.cache.DiskThumbnailCache;
import com.fde.google_drive_organizer.adapter.outbound.cache.ThumbnailCacheConfig;
import com.fde.google_drive_organizer.domain.port.outbound.ThumbnailRepository;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Component
public class GoogleDriveThumbnailRepository implements ThumbnailRepository {

    private static final Logger log = LoggerFactory.getLogger(GoogleDriveThumbnailRepository.class);

    private final DiskThumbnailCache cache;
    private final AccessTokenProvider accessTokenProvider;
    private final ThumbnailCacheConfig cacheConfig;

    public GoogleDriveThumbnailRepository(DiskThumbnailCache cache, AccessTokenProvider accessTokenProvider, ThumbnailCacheConfig cacheConfig) {
        this.cache = cache;
        this.accessTokenProvider = accessTokenProvider;
        this.cacheConfig = cacheConfig;
    }

    @Override
    public Optional<byte[]> getThumbnail(String fileId) {
        if (!cacheConfig.cacheThumbnailsInactive()) {
            Optional<byte[]> cachedThumbnail = cache.get(fileId);
            if (cachedThumbnail.isPresent()) {
                return cachedThumbnail;
            }
        }

        return fetchFromGoogleDrive(fileId);
    }

    private Optional<byte[]> fetchFromGoogleDrive(String fileId) {
        try {
            String accessToken = accessTokenProvider.getAccessToken();
            Drive driveService = buildDriveService(accessToken);

            // First, get the file metadata to retrieve the thumbnailLink
            File file = driveService.files().get(fileId)
                    .setFields("thumbnailLink")
                    .execute();

            String thumbnailLink = file.getThumbnailLink();
            if (thumbnailLink == null || thumbnailLink.isEmpty()) {
                log.debug("No thumbnail available for fileId: {}", fileId);
                return Optional.empty();
            }

            // Download the thumbnail image from the URL
            byte[] thumbnailData = downloadThumbnailFromUrl(thumbnailLink, accessToken);
            if (!cacheConfig.cacheThumbnailsInactive()) {
                cache.put(fileId, thumbnailData);
                log.debug("Fetched and cached thumbnail for fileId: {}", fileId);
            } else {
                log.debug("Fetched thumbnail for fileId: {} (caching disabled)", fileId);
            }
            return Optional.of(thumbnailData);

        } catch (IOException | GeneralSecurityException e) {
            log.error("Failed to fetch thumbnail from Google Drive for fileId: {}", fileId, e);
            return Optional.empty();
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

    private Drive buildDriveService(String accessToken) throws GeneralSecurityException, IOException {
        HttpRequestInitializer requestInitializer = request -> {
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        };

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Google Drive Organizer")
                .build();
    }
}
