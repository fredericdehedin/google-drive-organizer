package com.fde.google_drive_organizer.domain.exception;

public class DocumentContentExtractionException extends RuntimeException {

    private final String fileId;

    public DocumentContentExtractionException(String fileId, String message) {
        super(message);
        this.fileId = fileId;
    }

    public DocumentContentExtractionException(String fileId, String message, Throwable cause) {
        super(message, cause);
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }
}
