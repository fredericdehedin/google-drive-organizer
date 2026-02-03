package com.fde.google_drive_organizer.adapter.outbound.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ThumbnailCacheConfigTest.TestConfig.class)
@TestPropertySource(properties = {
        "thumbnail.cache.directory=/tmp/test-cache",
        "thumbnail.cache.cache-thumbnails-inactive=false"
})
class ThumbnailCacheConfigTest {

    @EnableConfigurationProperties(ThumbnailCacheConfig.class)
    static class TestConfig {}

    @Autowired
    private ThumbnailCacheConfig config;

    @Test
    void shouldBindDirectoryProperty() {
        assertThat(config.directory()).isEqualTo("/tmp/test-cache");
    }

    @Test
    void shouldBindCacheThumbnailsInactiveProperty() {
        assertThat(config.cacheThumbnailsInactive()).isFalse();
    }
}
