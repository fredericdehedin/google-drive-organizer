package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_folder.DriveFolderName;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MoveFileController {

    @GetMapping("/api/files/{fileId}/move")
    public ResponseEntity<Void> moveFile(
            @PathVariable String fileId,
            @RequestParam String folderName
    ) {
        System.out.println("Move file: fileId=" + new DriveFileId(fileId) + ", folderName=" + new DriveFolderName(folderName));
        return ResponseEntity.ok().build();
    }
}
