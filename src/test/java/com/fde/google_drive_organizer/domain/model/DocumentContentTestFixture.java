package com.fde.google_drive_organizer.domain.model;

public class DocumentContentTestFixture {

    public static DocumentContentBuilder aDocumentContent() {
        return new DocumentContentBuilder();
    }

    public static class DocumentContentBuilder {
        private String fileId = "default-file-id";
        private String textContent = "Default extracted text content";

        public DocumentContentBuilder withFileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public DocumentContentBuilder withTextContent(String textContent) {
            this.textContent = textContent;
            return this;
        }

        public DocumentContent build() {
            return new DocumentContent(fileId, textContent);
        }
    }
}
