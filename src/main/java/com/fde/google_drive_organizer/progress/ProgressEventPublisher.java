package com.fde.google_drive_organizer.progress;

import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.drive_folder.DriveTargetFolder;
import com.fde.google_drive_organizer.domain.suggest_target_folder_progress.SuggestTargetFolderProgressPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Backwards-compatible adapter.
 *
 * @deprecated use {@link SuggestTargetFolderProgressPublisher}
 */
@Deprecated
@Component
@Primary
public class ProgressEventPublisher {

    private final SuggestTargetFolderProgressPublisher delegate;

    public ProgressEventPublisher(SuggestTargetFolderProgressPublisher delegate) {
        this.delegate = delegate;
    }

    public void subscribe(DriveFileId driveFileId, SseEmitter emitter) {
        delegate.subscribe(driveFileId, emitter);
    }

    public void publish(DriveFileId driveFileId, ProgressStep step, String message) {
        delegate.publish(driveFileId, step, message);
    }

    public void publish(DriveFileId driveFileId, ProgressStep step, String message, DriveTargetFolder targetFolder) {
        delegate.publish(driveFileId, step, message, targetFolder);
    }
}
