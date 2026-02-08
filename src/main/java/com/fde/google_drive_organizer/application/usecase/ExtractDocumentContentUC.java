package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.application.port.inbound.ExtractDocumentContent;
import com.fde.google_drive_organizer.application.port.outbound.DocumentContentRepository;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import org.springframework.stereotype.Service;

@Service
public class ExtractDocumentContentUC implements ExtractDocumentContent {

    private final DocumentContentRepository documentContentRepository;

    public ExtractDocumentContentUC(DocumentContentRepository documentContentRepository) {
        this.documentContentRepository = documentContentRepository;
    }

    @Override
    public DocumentContent extract(String fileId) {
        return documentContentRepository.extractContent(fileId);
    }
}
