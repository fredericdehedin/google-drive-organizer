package com.fde.google_drive_organizer.domain.port.outbound;

public interface ThumbnailRepository {

    byte[] getThumbnail(String fileId);
}
