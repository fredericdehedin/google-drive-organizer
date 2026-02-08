package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.port.inbound.GetThumbnail;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/thumbnails")
public class ThumbnailController {

    private final GetThumbnail getThumbnail;

    public ThumbnailController(GetThumbnail getThumbnail) {
        this.getThumbnail = getThumbnail;
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable String fileId) {
        byte[] thumbnail = getThumbnail.get(fileId);

        if (thumbnail == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                .body(thumbnail);
    }
}
