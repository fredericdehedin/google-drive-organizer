package com.fde.google_drive_organizer.application.port.outbound;

import com.fde.google_drive_organizer.domain.model.DocumentContent;

public interface DocumentContentRepository {

    DocumentContent extractContent(String fileId);
}
