package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.port.inbound.MoveDocumentToFolder;
import com.fde.google_drive_organizer.domain.model.DriveFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArchiveController {

    private final MoveDocumentToFolder moveDocumentToFolder;

    public ArchiveController(MoveDocumentToFolder moveDocumentToFolder) {
        this.moveDocumentToFolder = moveDocumentToFolder;
    }

    @GetMapping("/api/files/{fileId}/archive")
    public ResponseEntity<Void> archiveFile(
            @PathVariable String fileId,
            @RequestParam String fileName
    ) {
        DriveFile driveFile = new DriveFile(fileId, fileName, null, null, null);
        moveDocumentToFolder.move(driveFile);
        return ResponseEntity.ok().build();
    }
}
