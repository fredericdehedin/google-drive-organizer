package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.port.inbound.SuggestTargetFolderService;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileName;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileRef;
import com.fde.google_drive_organizer.progress.FileId;
import com.fde.google_drive_organizer.progress.ProgressEventPublisher;
import com.fde.google_drive_organizer.progress.ProgressStep;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SuggestTargetFolderController {

    private final SuggestTargetFolderService suggestTargetFolderService;
    private final ProgressEventPublisher publisher;

    public SuggestTargetFolderController(SuggestTargetFolderService suggestTargetFolderService, ProgressEventPublisher publisher) {
        this.suggestTargetFolderService = suggestTargetFolderService;
        this.publisher = publisher;
    }

    @GetMapping("/api/files/{fileId}/suggest-target-folder")
    public ResponseEntity<Void> startSuggestTargetFolder(
            @PathVariable String fileId,
            @RequestParam String fileName
    ) {
        publisher.publish(new FileId(fileId), ProgressStep.STARTED, "Starting archive...");
        DriveFileRef driveFileRef = new DriveFileRef(new DriveFileId(fileId), new DriveFileName(fileName));
        suggestTargetFolderService.suggestTargetFolder(driveFileRef);
        return ResponseEntity.ok().build();
    }
}
