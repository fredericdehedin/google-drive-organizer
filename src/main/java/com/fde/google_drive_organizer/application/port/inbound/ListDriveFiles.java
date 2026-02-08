package com.fde.google_drive_organizer.application.port.inbound;

import com.fde.google_drive_organizer.domain.model.DriveFile;
import java.util.List;

@FunctionalInterface
public interface ListDriveFiles {
    List<DriveFile> list();
}
