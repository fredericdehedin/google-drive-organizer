package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.port.inbound.ExtractDocumentContent;
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

    private final ExtractDocumentContent extractDocumentContent;

    public ArchiveController(ExtractDocumentContent extractDocumentContent) {
        this.extractDocumentContent = extractDocumentContent;
    }

    @GetMapping("/api/files/{fileId}/archive")
    public ResponseEntity<Void> archiveFile(@PathVariable String fileId) {
        DocumentContent content = extractDocumentContent.extract(fileId);
        LOGGER.info("Document content for file " + fileId + ":");
        LOGGER.info(content.textContent());
        return ResponseEntity.ok().build();
    }
}
