package com.fde.google_drive_organizer.domain.drive_file.document_content;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import net.datafaker.Faker;

public class DriveFileDocumentContentTestFixture {

    private static final Faker FAKER = new Faker();

    public static DriveFileDocumentContentBuilder aDriveFileDocumentContent() {
        return new DriveFileDocumentContentBuilder();
    }

    public static class DriveFileDocumentContentBuilder {
        private String fileId = FAKER.internet().uuid();
        private String textContent = FAKER.lorem().paragraph();

        public DriveFileDocumentContentBuilder withFileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public DriveFileDocumentContentBuilder withTextContent(String textContent) {
            this.textContent = textContent;
            return this;
        }

        public DriveFileDocumentContent build() {
            return new DriveFileDocumentContent(new DriveFileId(fileId), new DriveFileDocumentContentText(textContent));
        }
    }
}
