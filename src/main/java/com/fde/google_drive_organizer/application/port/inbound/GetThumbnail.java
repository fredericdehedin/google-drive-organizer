package com.fde.google_drive_organizer.application.port.inbound;

@FunctionalInterface
public interface GetThumbnail {
    byte[] get(String fileId);
}
