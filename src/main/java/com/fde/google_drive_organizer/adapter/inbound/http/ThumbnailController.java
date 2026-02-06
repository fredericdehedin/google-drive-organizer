package com.fde.google_drive_organizer.adapter.inbound.http;

import com.fde.google_drive_organizer.application.usecase.GetThumbnailUC;
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

    private final GetThumbnailUC getThumbnailUC;

    public ThumbnailController(GetThumbnailUC getThumbnailUC) {
        this.getThumbnailUC = getThumbnailUC;
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable String fileId) {
        byte[] thumbnail = getThumbnailUC.execute(fileId);

        if (thumbnail == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                .body(thumbnail);
    }
}
