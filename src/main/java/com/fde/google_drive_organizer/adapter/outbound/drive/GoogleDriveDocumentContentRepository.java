package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.adapter.outbound.tika.DocumentParser;
import com.fde.google_drive_organizer.application.port.outbound.DocumentContentRepository;
import com.fde.google_drive_organizer.domain.exception.DocumentContentExtractionException;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.google.api.services.drive.Drive;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Repository
public class GoogleDriveDocumentContentRepository implements DocumentContentRepository {

    private static final Logger log = LoggerFactory.getLogger(GoogleDriveDocumentContentRepository.class);

    private final Drive drive;
    private final DocumentParser textParser;
    private final DocumentParser ocrParser;

    public GoogleDriveDocumentContentRepository(
            Drive drive,
            @Qualifier("tikaPdfTextDocumentParser") DocumentParser textParser,
            @Qualifier("tesseractOcrDocumentParser") DocumentParser ocrParser) {
        this.drive = drive;
        this.textParser = textParser;
        this.ocrParser = ocrParser;
    }

    @Override
    public DocumentContent extractContent(String fileId) {
        try (InputStream inputStream = drive.files().get(fileId).executeMediaAsInputStream()) {
            byte[] content = inputStream.readAllBytes();

            String extractedText = textParser.parseToText(new ByteArrayInputStream(content));

            if (extractedText.isEmpty()) {
                log.info("Normal text extraction returned empty result for file {}, attempting OCR", fileId);
                extractedText = ocrParser.parseToText(new ByteArrayInputStream(content));
            }

            log.debug("Extracted content for fileId: {}, length: {}", fileId, extractedText.length());
            return new DocumentContent(fileId, extractedText);

        } catch (IOException | TikaException e) {
            throw new DocumentContentExtractionException(fileId, "Failed to extract content from Google Drive", e);
        }
    }
}
