package com.fde.google_drive_organizer.adapter.outbound.cache;

import com.fde.google_drive_organizer.application.port.outbound.ThumbnailRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import static com.fde.google_drive_organizer.adapter.outbound.cache.ThumbnailCacheConfiguration.THUMBNAIL_CACHE_NAME;

@Repository
@Primary
public class ThumbnailCacheableRepository implements ThumbnailRepository {

    private final ThumbnailRepository thumbnailRepositoryDelegate;

    public ThumbnailCacheableRepository(ThumbnailRepository thumbnailRepositoryDelegate) {
        this.thumbnailRepositoryDelegate = thumbnailRepositoryDelegate;
    }

    @Override
    @Cacheable(value = THUMBNAIL_CACHE_NAME)
    public byte[] getThumbnail(String fileId) {
        return thumbnailRepositoryDelegate.getThumbnail(fileId);
    }
}
