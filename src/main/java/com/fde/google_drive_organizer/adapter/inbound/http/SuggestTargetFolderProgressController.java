package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.suggest_target_folder_progress.SuggestTargetFolderProgressPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
public class SuggestTargetFolderProgressController {

    private final SuggestTargetFolderProgressPublisher publisher;

    public SuggestTargetFolderProgressController(SuggestTargetFolderProgressPublisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping(value = "/api/files/{fileId}/suggest-target-folder/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter suggestTargetFolderProgress(@PathVariable String fileId) throws IOException {
        //TODO: move out of the controller..
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        emitter.send(SseEmitter.event().comment("connected"));
        publisher.subscribe(new DriveFileId(fileId), emitter);
        return emitter;
    }
}
