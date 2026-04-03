package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.adapter.outbound.tika.DocumentParser;
import com.fde.google_drive_organizer.application.port.outbound.DocumentContentRepository;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.exception.DocumentContentExtractionException;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.fde.google_drive_organizer.domain.suggest_target_folder_progress.SuggestTargetFolderProgressPublisher;
import com.fde.google_drive_organizer.progress.ProgressStep;
import com.google.api.services.drive.Drive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Repository
public class GoogleDriveDocumentContentRepository implements DocumentContentRepository {

    private static final Logger log = LoggerFactory.getLogger(GoogleDriveDocumentContentRepository.class);

    private final ObjectProvider<Drive> driveProvider;
    private final DocumentParser textParser;
    private final DocumentParser ocrParser;
    private final SuggestTargetFolderProgressPublisher publisher;

    public GoogleDriveDocumentContentRepository(
            ObjectProvider<Drive> driveProvider,
            @Qualifier("tikaPdfTextDocumentParser") DocumentParser textParser,
            @Qualifier("tesseractOcrDocumentParser") DocumentParser ocrParser,
            SuggestTargetFolderProgressPublisher publisher) {
        this.driveProvider = driveProvider;
        this.textParser = textParser;
        this.ocrParser = ocrParser;
        this.publisher = publisher;
    }

    @Override
    public DocumentContent extractContent(String fileId) {
        DriveFileId driveFileId = new DriveFileId(fileId);
        try {
            publisher.publish(driveFileId, ProgressStep.DOWNLOADING, "Downloading file...");
            byte[] content;
            try (InputStream inputStream = driveProvider.getObject().files().get(fileId).executeMediaAsInputStream()) {
                content = inputStream.readAllBytes();
            }

            publisher.publish(driveFileId, ProgressStep.EXTRACTING_TEXT, "Extracting text...");
            String extractedText = textParser.parseToText(new ByteArrayInputStream(content));

            if (extractedText.isBlank()) {
                log.info("Normal text extraction returned empty result for file {}, attempting OCR", fileId);
                publisher.publish(driveFileId, ProgressStep.OCR, "Running OCR...");
                extractedText = ocrParser.parseToText(new ByteArrayInputStream(content));
            }

            log.info("Extracted content for fileId: {}, length: {}", fileId, extractedText.length());
            return new DocumentContent(fileId, extractedText);

        } catch (IOException e) {
            throw new DocumentContentExtractionException(fileId, "Failed to download content from Google Drive", e);
        }
    }
}
