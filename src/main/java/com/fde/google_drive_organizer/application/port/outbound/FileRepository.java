package com.fde.google_drive_organizer.application.port.outbound;

import com.fde.google_drive_organizer.domain.drive_file.DriveFile;

import java.util.List;

public interface FileRepository {

    List<DriveFile> getFilesInRootFolder();
}
