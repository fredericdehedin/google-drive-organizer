package com.fde.google_drive_organizer.domain.drive_file;

import com.fde.google_drive_organizer.domain.drive_file.file.DriveFile;
import com.fde.google_drive_organizer.domain.drive_file.file.DriveIconLink;
import com.fde.google_drive_organizer.domain.drive_file.file.DriveMimeType;
import com.fde.google_drive_organizer.domain.drive_file.file.DriveThumbnailLink;
import com.fde.google_drive_organizer.domain.drive_file.ref.DriveFileRef;
import net.datafaker.Faker;

public class DriveFileTestFixture {

    private static final Faker FAKER = new Faker();

    public static DriveFileRefBuilder aDriveFileRef() {
        return new DriveFileRefBuilder();
    }

    public static DriveFileBuilder aDriveFile() {
        return new DriveFileBuilder();
    }

    public static class DriveFileRefBuilder {
        private String id = FAKER.internet().uuid();
        private String name = FAKER.file().fileName();

        public DriveFileRefBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public DriveFileRefBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DriveFileRef build() {
            return new DriveFileRef(new DriveFileId(id), new DriveFileName(name));
        }
    }

    public static class DriveFileBuilder {
        private String id = FAKER.internet().uuid();
        private String name = FAKER.file().fileName();
        private String mimeType = "application/octet-stream";
        private String iconLink = "https://drive-thirdparty.googleusercontent.com/16/type/application/octet-stream";
        private String thumbnailLink = null;

        public DriveFileBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public DriveFileBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DriveFileBuilder withMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public DriveFileBuilder withIconLink(String iconLink) {
            this.iconLink = iconLink;
            return this;
        }

        public DriveFileBuilder withThumbnailLink(String thumbnailLink) {
            this.thumbnailLink = thumbnailLink;
            return this;
        }

        public DriveFile build() {
            return new DriveFile(
                    new DriveFileId(id),
                    new DriveFileName(name),
                    new DriveMimeType(mimeType),
                    new DriveIconLink(iconLink),
                    new DriveThumbnailLink(thumbnailLink)
            );
        }
    }
}
