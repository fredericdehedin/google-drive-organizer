package com.fde.google_drive_organizer.domain.suggest_target_folder_progress;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_folder.DriveFolderId;
import com.fde.google_drive_organizer.domain.drive_folder.DriveFolderName;
import com.fde.google_drive_organizer.domain.drive_folder.DriveTargetFolder;
import com.fde.google_drive_organizer.progress.ProgressEvent;
import com.fde.google_drive_organizer.progress.ProgressStep;
import com.fde.google_drive_organizer.progress.ProgressSubscribers;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SuggestTargetFolderProgressPublisher {

    private final ConcurrentHashMap<DriveFileId, ProgressSubscribers> subscribers = new ConcurrentHashMap<>();

    public void subscribe(DriveFileId driveFileId, SseEmitter emitter) {
        ProgressSubscribers subs = subscribers.computeIfAbsent(driveFileId, id -> new ProgressSubscribers());
        subs.add(emitter);
        emitter.onCompletion(() -> unsubscribe(driveFileId, emitter));
        emitter.onTimeout(() -> unsubscribe(driveFileId, emitter));
        emitter.onError(e -> unsubscribe(driveFileId, emitter));
    }

    public void publish(DriveFileId driveFileId, ProgressStep step, String message) {
        publish(driveFileId, step, message, new DriveTargetFolder(new DriveFolderId(null), new DriveFolderName(null)));
    }

    public void publish(DriveFileId driveFileId, ProgressStep step, String message, DriveTargetFolder targetFolder) {
        ProgressSubscribers subs = subscribers.get(driveFileId);
        if (subs != null) {
            subs.broadcast(new ProgressEvent(driveFileId, step, message, targetFolder, Instant.now()));
        }
    }

    private void unsubscribe(DriveFileId driveFileId, SseEmitter emitter) {
        ProgressSubscribers subs = subscribers.get(driveFileId);
        if (subs != null) {
            subs.remove(emitter);
            if (subs.isEmpty()) {
                subscribers.remove(driveFileId);
            }
        }
    }
}
