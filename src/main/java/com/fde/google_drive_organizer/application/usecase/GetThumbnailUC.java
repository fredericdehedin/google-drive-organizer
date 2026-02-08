package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.application.port.inbound.GetThumbnail;
import com.fde.google_drive_organizer.application.port.outbound.ThumbnailRepository;
import org.springframework.stereotype.Service;

@Service
public class GetThumbnailUC implements GetThumbnail {

    private final ThumbnailRepository thumbnailRepository;

    public GetThumbnailUC(ThumbnailRepository thumbnailRepository) {
        this.thumbnailRepository = thumbnailRepository;
    }

    @Override
    public byte[] get(String fileId) {
        return thumbnailRepository.getThumbnail(fileId);
    }
}
