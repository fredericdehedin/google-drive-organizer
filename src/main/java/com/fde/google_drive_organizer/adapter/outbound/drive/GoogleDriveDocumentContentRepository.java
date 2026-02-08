package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.adapter.outbound.tika.TikaDocumentParser;
import com.fde.google_drive_organizer.application.port.outbound.DocumentContentRepository;
import com.fde.google_drive_organizer.domain.exception.DocumentContentExtractionException;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

@Repository
public class GoogleDriveDocumentContentRepository implements DocumentContentRepository {

    private static final Logger log = LoggerFactory.getLogger(GoogleDriveDocumentContentRepository.class);

    private final AccessTokenProvider accessTokenProvider;
    private final TikaDocumentParser tikaDocumentParser;

    public GoogleDriveDocumentContentRepository(
            AccessTokenProvider accessTokenProvider,
            TikaDocumentParser tikaDocumentParser) {
        this.accessTokenProvider = accessTokenProvider;
        this.tikaDocumentParser = tikaDocumentParser;
    }

    @Override
    public DocumentContent extractContent(String fileId) {
        try {
            String accessToken = accessTokenProvider.getAccessToken();
            if (accessToken == null) {
                throw new DocumentContentExtractionException(fileId, "No access token available");
            }

            Drive driveService = buildDriveService(accessToken);

            try (InputStream inputStream = driveService.files().get(fileId)
                    .executeMediaAsInputStream()) {

                String textContent = tikaDocumentParser.parseToText(inputStream);
                log.debug("Extracted content for fileId: {}, length: {}", fileId, textContent.length());
                return new DocumentContent(fileId, textContent);
            }

        } catch (IOException | GeneralSecurityException | TikaException e) {
            throw new DocumentContentExtractionException(fileId, "Failed to extract content from Google Drive", e);
        }
    }

    private Drive buildDriveService(String accessToken) throws GeneralSecurityException, IOException {
        HttpRequestInitializer requestInitializer = request
                -> request.getHeaders().setAuthorization("Bearer " + accessToken);

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Google Drive Organizer")
                .build();
    }
}
