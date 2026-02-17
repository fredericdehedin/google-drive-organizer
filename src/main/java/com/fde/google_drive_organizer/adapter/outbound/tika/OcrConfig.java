package com.fde.google_drive_organizer.adapter.outbound.tika;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ocr")
public record OcrConfig(
    String language
) {
    public OcrConfig {
        if (language == null || language.isBlank()) {
            language = "deu+eng";
        }
    }
}
