package com.fde.google_drive_organizer.domain.suggest_target_folder_progress;

public enum ProgressStep {
    STARTED,
    DOWNLOADING,
    EXTRACTING_TEXT,
    OCR,
    ANALYZING,
    DONE,
    FAILED
}
