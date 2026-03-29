package com.fde.google_drive_organizer.application.port.inbound;

import com.fde.google_drive_organizer.domain.model.DriveFile;

@FunctionalInterface
public interface MoveDocumentToFolder {

    void move(DriveFile driveFile);
}
