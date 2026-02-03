package com.fde.google_drive_organizer.adapter.outbound.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "thumbnail.cache")
public record ThumbnailCacheConfig(
        @DefaultValue("./cache/thumbnails") String directory
) {}
