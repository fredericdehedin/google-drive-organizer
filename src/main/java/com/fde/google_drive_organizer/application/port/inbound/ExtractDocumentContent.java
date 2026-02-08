package com.fde.google_drive_organizer.application.port.inbound;

import com.fde.google_drive_organizer.domain.model.DocumentContent;

@FunctionalInterface
public interface ExtractDocumentContent {
    DocumentContent extract(String fileId);
}
