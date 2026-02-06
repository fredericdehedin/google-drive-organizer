package com.fde.google_drive_organizer.adapter.outbound.cache;

import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Configuration
@EnableCaching
public class ThumbnailCacheConfiguration {

    public static final String THUMBNAIL_CACHE_NAME = "thumbnails";

    @Bean
    public CacheManager cacheManager(DiskThumbnailCache diskThumbnailCache) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(new DiskThumbnailCacheAdapter(diskThumbnailCache)));
        return cacheManager;
    }

    static class DiskThumbnailCacheAdapter extends AbstractValueAdaptingCache {

        private final DiskThumbnailCache diskCache;

        protected DiskThumbnailCacheAdapter(DiskThumbnailCache diskCache) {
            super(true);
            this.diskCache = diskCache;
        }

        @Override
        @Nonnull
        public String getName() {
            return THUMBNAIL_CACHE_NAME;
        }

        @Override
        @Nonnull
        public Object getNativeCache() {
            return diskCache;
        }

        @Override
        protected Object lookup(@NonNull Object key) {
            String fileId = (String) key;
            Optional<byte[]> cached = diskCache.get(fileId);
            return cached.orElse(null);
        }

        @Override
        public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
            Object value = lookup(key);
            if (value != null) {
                return (T) fromStoreValue(value);
            }

            try {
                T loadedValue = valueLoader.call();
                put(key, loadedValue);
                return loadedValue;
            } catch (Exception e) {
                throw new Cache.ValueRetrievalException(key, valueLoader, e);
            }
        }

        @Override
        public void put(@NonNull Object key, Object value) {
            if (value != null) {
                String fileId = (String) key;
                byte[] data = (byte[]) value;
                diskCache.put(fileId, data);
            }
        }

        @Override
        public void evict(@NonNull Object key) {
            // No eviction support for now
        }

        @Override
        public void clear() {
            // No clear support for now
        }
    }
}
