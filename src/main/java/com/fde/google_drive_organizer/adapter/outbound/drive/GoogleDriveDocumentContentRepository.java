package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.adapter.outbound.tika.TikaDocumentParser;
import com.fde.google_drive_organizer.application.port.outbound.DocumentContentRepository;
import com.fde.google_drive_organizer.domain.exception.DocumentContentExtractionException;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.google.api.services.drive.Drive;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;

@Repository
public class GoogleDriveDocumentContentRepository implements DocumentContentRepository {

    private static final Logger log = LoggerFactory.getLogger(GoogleDriveDocumentContentRepository.class);

    private final Drive drive;
    private final TikaDocumentParser tikaDocumentParser;

    public GoogleDriveDocumentContentRepository(
            Drive drive,
            TikaDocumentParser tikaDocumentParser) {
        this.drive = drive;
        this.tikaDocumentParser = tikaDocumentParser;
    }

    @Override
    public DocumentContent extractContent(String fileId) {
        try {
            try (InputStream inputStream = drive.files().get(fileId)
                    .executeMediaAsInputStream()) {

                String textContent = tikaDocumentParser.parseToText(inputStream);
                log.debug("Extracted content for fileId: {}, length: {}", fileId, textContent.length());
                return new DocumentContent(fileId, textContent);
            }

        } catch (IOException | TikaException e) {
            throw new DocumentContentExtractionException(fileId, "Failed to extract content from Google Drive", e);
        }
    }
}
