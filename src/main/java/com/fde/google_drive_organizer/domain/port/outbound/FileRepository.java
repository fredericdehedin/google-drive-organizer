package com.fde.google_drive_organizer.domain.port.outbound;

import com.fde.google_drive_organizer.domain.model.DriveFile;

import java.util.List;

public interface FileRepository {

    List<DriveFile> getFilesInCheckInFolder();
}
