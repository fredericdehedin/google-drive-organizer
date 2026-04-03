package com.fde.google_drive_organizer.domain.drive_file;

//TODO: move to testFixtures
public class DriveFileTestFixture {

    public static DriveFileRefBuilder aDriveFileRef() {
        return new DriveFileRefBuilder();
    }

    public static DriveFileBuilder aDriveFile() {
        return new DriveFileBuilder();
    }

    public static class DriveFileRefBuilder {
        private String id = "default-id";
        private String name = "default-file.txt";

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
        private String id = "default-id";
        private String name = "default-file.txt";
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
                    mimeType == null ? null : new DriveMimeType(mimeType),
                    iconLink == null ? null : new DriveIconLink(iconLink),
                    thumbnailLink == null ? null : new DriveThumbnailLink(thumbnailLink)
            );
        }
    }
}
