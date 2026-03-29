package com.fde.google_drive_organizer.adapter.outbound.drive;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "drive")
public record DriveConfig(
        String rootFolderId
) {}
