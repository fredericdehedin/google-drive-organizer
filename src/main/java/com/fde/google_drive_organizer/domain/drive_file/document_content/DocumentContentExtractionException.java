package com.fde.google_drive_organizer.domain.drive_file.document_content;

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

    /**
     * Convenience constructor for use when the failing code path doesn't have a Google Drive fileId.
     */
    public DocumentContentExtractionException(String message, Throwable cause) {
        super(message, cause);
        this.fileId = null;
    }

    public String getFileId() {
        return fileId;
    }
}
