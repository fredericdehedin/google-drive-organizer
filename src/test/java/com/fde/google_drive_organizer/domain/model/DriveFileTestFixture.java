package com.fde.google_drive_organizer.domain.model;

public class DriveFileTestFixture {

    public static DriveFileBuilder aDriveFile() {
        return new DriveFileBuilder();
    }

    public static class DriveFileBuilder {
        private String id = "default-id";
        private String name = "default-file.txt";

        public DriveFileBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public DriveFileBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DriveFile build() {
            return new DriveFile(id, name);
        }
    }
}
