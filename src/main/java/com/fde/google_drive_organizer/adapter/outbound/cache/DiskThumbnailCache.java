package com.fde.google_drive_organizer.adapter.outbound.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Component
public class DiskThumbnailCache {

    private static final Logger log = LoggerFactory.getLogger(DiskThumbnailCache.class);

    private final Path cacheDirectory;

    public DiskThumbnailCache(ThumbnailCacheConfig config) {
        this.cacheDirectory = Paths.get(config.directory());
        initializeCacheDirectory();
    }

    private void initializeCacheDirectory() {
        try {
            if (!Files.exists(cacheDirectory)) {
                Files.createDirectories(cacheDirectory);
                log.info("Created thumbnail cache directory: {}", cacheDirectory);
            }
        } catch (IOException e) {
            log.error("Failed to create cache directory: {}", cacheDirectory, e);
        }
    }

    public Optional<byte[]> get(String fileId) {
        Path cachedFile = getCachePath(fileId);
        if (Files.exists(cachedFile)) {
            try {
                byte[] data = Files.readAllBytes(cachedFile);
                log.debug("Cache hit for fileId: {}", fileId);
                return Optional.of(data);
            } catch (IOException e) {
                log.error("Failed to read cached thumbnail for fileId: {}", fileId, e);
            }
        }
        log.debug("Cache miss for fileId: {}", fileId);
        return Optional.empty();
    }

    public void put(String fileId, byte[] data) {
        Path cachedFile = getCachePath(fileId);
        try {
            Files.write(cachedFile, data);
            log.debug("Cached thumbnail for fileId: {}", fileId);
        } catch (IOException e) {
            log.error("Failed to cache thumbnail for fileId: {}", fileId, e);
        }
    }

    private Path getCachePath(String fileId) {
        String sanitizedFileId = sanitizeFileId(fileId);
        return cacheDirectory.resolve(sanitizedFileId + ".jpg");
    }

    private String sanitizeFileId(String fileId) {
        return fileId.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
