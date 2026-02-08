package com.fde.google_drive_organizer.application.port.outbound;

public interface ThumbnailRepository {

    byte[] getThumbnail(String fileId);
}
