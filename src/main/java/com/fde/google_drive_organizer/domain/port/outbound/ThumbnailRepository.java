package com.fde.google_drive_organizer.domain.port.outbound;

import java.util.Optional;

public interface ThumbnailRepository {
    Optional<byte[]> getThumbnail(String fileId);
}
