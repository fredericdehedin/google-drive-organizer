package com.fde.google_drive_organizer.domain.drive_folder;

import net.datafaker.Faker;

public class DriveTargetFolderTestFixture {

    private static final Faker FAKER = new Faker();

    public static DriveTargetFolderBuilder aDriveTargetFolder() {
        return new DriveTargetFolderBuilder();
    }

    public static class DriveTargetFolderBuilder {
        private String id = null;
        private String name = FAKER.file().fileName();

        public DriveTargetFolderBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public DriveTargetFolderBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DriveTargetFolder build() {
            return new DriveTargetFolder(new DriveFolderId(id), new DriveFolderName(name));
        }
    }
}
