package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.domain.port.outbound.ThumbnailRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetThumbnailUC {

    private final ThumbnailRepository thumbnailRepository;

    public GetThumbnailUC(ThumbnailRepository thumbnailRepository) {
        this.thumbnailRepository = thumbnailRepository;
    }

    public Optional<byte[]> execute(String fileId) {
        return thumbnailRepository.getThumbnail(fileId);
    }
}
