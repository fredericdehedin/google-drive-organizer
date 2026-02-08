package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.usecase.ExtractDocumentContentUC;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArchiveController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveController.class);

    private final ExtractDocumentContentUC extractDocumentContentUC;

    public ArchiveController(ExtractDocumentContentUC extractDocumentContentUC) {
        this.extractDocumentContentUC = extractDocumentContentUC;
    }

    @GetMapping("/api/files/{fileId}/archive")
    public ResponseEntity<Void> archiveFile(@PathVariable String fileId) {
        DocumentContent content = extractDocumentContentUC.execute(fileId);
        LOGGER.info("Document content for file " + fileId + ":");
        LOGGER.info(content.textContent());
        return ResponseEntity.ok().build();
    }
}
