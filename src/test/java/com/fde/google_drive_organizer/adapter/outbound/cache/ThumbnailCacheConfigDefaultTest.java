package com.fde.google_drive_organizer.adapter.outbound.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ThumbnailCacheConfigDefaultTest.TestConfig.class)
class ThumbnailCacheConfigDefaultTest {

    @EnableConfigurationProperties(ThumbnailCacheConfig.class)
    static class TestConfig {}

    @Autowired
    private ThumbnailCacheConfig config;

    @Test
    void shouldUseDefaultDirectory() {
        assertThat(config.directory()).isEqualTo("./cache/thumbnails");
    }

    @Test
    void shouldDefaultCacheThumbnailsInactiveToTrue() {
        assertThat(config.cacheThumbnailsInactive()).isTrue();
    }
}
