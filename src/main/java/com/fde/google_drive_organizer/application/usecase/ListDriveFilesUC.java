package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.application.port.inbound.ListDriveFiles;
import com.fde.google_drive_organizer.domain.model.DriveFile;
import com.fde.google_drive_organizer.application.port.outbound.FileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListDriveFilesUC implements ListDriveFiles {

    private final FileRepository fileRepository;

    public ListDriveFilesUC(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public List<DriveFile> list() {
        return fileRepository.getFilesInCheckInFolder();
    }
}
