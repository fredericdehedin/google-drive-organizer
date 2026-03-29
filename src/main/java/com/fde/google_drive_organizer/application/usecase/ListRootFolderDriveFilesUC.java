package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.application.port.inbound.ListDriveFiles;
import com.fde.google_drive_organizer.application.port.outbound.FileRepository;
import com.fde.google_drive_organizer.domain.model.DriveFile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListRootFolderDriveFilesUC implements ListDriveFiles {

    private final FileRepository fileRepository;

    public ListRootFolderDriveFilesUC(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public List<DriveFile> list() {
        return fileRepository.getFilesInRootFolder();
    }
}
