package com.fde.google_drive_organizer.adapter.outbound.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DiskThumbnailCacheTest {

    @TempDir
    Path tempDir;

    private DiskThumbnailCache cache;

    @BeforeEach
    void setUp() {
        ThumbnailCacheConfig config = new ThumbnailCacheConfig(tempDir.toString());
        cache = new DiskThumbnailCache(config);
    }

    @Test
    void shouldReturnEmptyWhenCacheMiss() {
        Optional<byte[]> result = cache.get("non-existent-file-id");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnDataWhenCacheHit() {
        String fileId = "test-file-id";
        byte[] data = new byte[]{1, 2, 3, 4, 5};

        cache.put(fileId, data);
        Optional<byte[]> result = cache.get(fileId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(data);
    }

    @Test
    void shouldSanitizeFileId() throws IOException {
        String unsafeFileId = "file/with\\unsafe:chars*";
        byte[] data = new byte[]{1, 2, 3};

        cache.put(unsafeFileId, data);

        assertThat(Files.list(tempDir).count()).isEqualTo(1);
        Optional<byte[]> result = cache.get(unsafeFileId);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(data);
    }

    @Test
    void shouldCreateCacheDirectoryIfNotExists() {
        Path nonExistentDir = tempDir.resolve("new-cache-dir");
        assertThat(Files.exists(nonExistentDir)).isFalse();

        ThumbnailCacheConfig config = new ThumbnailCacheConfig(nonExistentDir.toString());
        new DiskThumbnailCache(config);

        assertThat(Files.exists(nonExistentDir)).isTrue();
    }

    @Test
    void shouldOverwriteExistingCachedFile() {
        String fileId = "test-file-id";
        byte[] originalData = new byte[]{1, 2, 3};
        byte[] newData = new byte[]{4, 5, 6};

        cache.put(fileId, originalData);
        cache.put(fileId, newData);

        Optional<byte[]> result = cache.get(fileId);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(newData);
    }
}
