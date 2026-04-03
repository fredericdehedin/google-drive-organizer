package com.fde.google_drive_organizer.application.services;

import com.fde.google_drive_organizer.application.port.inbound.ExtractDocumentContent;
import com.fde.google_drive_organizer.application.port.outbound.DocumentContentRepository;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_file.document_content.DriveFileDocumentContent;
import org.springframework.stereotype.Service;

@Service
public class ExtractDocumentContentService implements ExtractDocumentContent {

    private final DocumentContentRepository documentContentRepository;

    public ExtractDocumentContentService(DocumentContentRepository documentContentRepository) {
        this.documentContentRepository = documentContentRepository;
    }

    @Override
    public DriveFileDocumentContent extract(DriveFileId fileId) {
        return documentContentRepository.extractContent(fileId);
    }
}
