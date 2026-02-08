package com.fde.google_drive_organizer.domain.model;

import java.util.Objects;

public record DocumentContent(
        String fileId,
        String textContent
) {
    public DocumentContent {
        Objects.requireNonNull(fileId, "fileId must not be null");
        Objects.requireNonNull(textContent, "textContent must not be null");
        if (fileId.isBlank()) {
            throw new IllegalArgumentException("fileId must not be blank");
        }
    }
}
