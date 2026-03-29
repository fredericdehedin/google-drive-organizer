package com.fde.google_drive_organizer.adapter.outbound.tika;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "ocr")
public record OcrConfig(
    @DefaultValue("deu+eng") String language,
    @DefaultValue("C:\\Program Files\\Tesseract-OCR") String tesseractPath,
    @DefaultValue("C:\\Program Files\\Tesseract-OCR\\tessdata") String tessdataPath
) {}
