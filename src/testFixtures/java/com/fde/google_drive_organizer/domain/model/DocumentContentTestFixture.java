package com.fde.google_drive_organizer.domain.model;

import net.datafaker.Faker;

public class DocumentContentTestFixture {

    private static final Faker FAKER = new Faker();

    public static DocumentContentBuilder aDocumentContent() {
        return new DocumentContentBuilder();
    }

    public static class DocumentContentBuilder {
        private String fileId = FAKER.internet().uuid();
        private String textContent = FAKER.lorem().paragraph();

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
