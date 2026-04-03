package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.port.inbound.MoveDocumentToFolder;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileName;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileRef;
import com.fde.google_drive_organizer.progress.FileId;
import com.fde.google_drive_organizer.progress.ProgressEventPublisher;
import com.fde.google_drive_organizer.progress.ProgressStep;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
public class ArchiveController {

    private final MoveDocumentToFolder moveDocumentToFolder;
    private final ProgressEventPublisher publisher;

    public ArchiveController(MoveDocumentToFolder moveDocumentToFolder, ProgressEventPublisher publisher) {
        this.moveDocumentToFolder = moveDocumentToFolder;
        this.publisher = publisher;
    }

    @GetMapping("/api/files/{fileId}/archive")
    public ResponseEntity<Void> archiveFile(
            @PathVariable String fileId,
            @RequestParam String fileName
    ) {
        publisher.publish(new FileId(fileId), ProgressStep.STARTED, "Starting archive...");
        DriveFileRef driveFileRef = new DriveFileRef(new DriveFileId(fileId), new DriveFileName(fileName));
        moveDocumentToFolder.move(driveFileRef);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/api/files/{fileId}/archive/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter archiveProgress(@PathVariable String fileId) throws IOException {
        //TODO: move out of the controller..
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        emitter.send(SseEmitter.event().comment("connected"));
        publisher.subscribe(new FileId(fileId), emitter);
        return emitter;
    }
}
