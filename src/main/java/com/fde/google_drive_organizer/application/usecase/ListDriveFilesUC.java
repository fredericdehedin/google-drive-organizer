package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.domain.model.DriveFile;
import com.fde.google_drive_organizer.domain.port.outbound.FileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListDriveFilesUC {

    private final FileRepository fileRepository;

    public ListDriveFilesUC(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public List<DriveFile> execute() {
        return fileRepository.getFilesInCheckInFolder();
    }
}
