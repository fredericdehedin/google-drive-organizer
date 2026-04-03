package com.fde.google_drive_organizer.progress;

import java.time.Instant;

public record ProgressEvent(
        FileId fileId,
        ProgressStep step,
        String message,
        Instant timestamp
) {}
