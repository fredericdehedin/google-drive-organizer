package com.fde.google_drive_organizer.adapter.outbound.tika;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = OcrConfigTest.TestConfig.class)
@TestPropertySource(properties = {
        "ocr.language=fra",
        "ocr.tesseract-path=C:\\\\Program Files\\\\Tesseract-OCR",
        "ocr.tessdata-path=C:\\\\Program Files\\\\Tesseract-OCR\\\\tessdata"
})
class OcrConfigTest {

    @EnableConfigurationProperties(OcrConfig.class)
    static class TestConfig {}

    @Autowired
    private OcrConfig config;

    @Test
    void shouldBindLanguageProperty() {
        assertThat(config.language()).isEqualTo("fra");
    }

    @Test
    void shouldBindTesseractPath() {
        assertThat(config.tesseractPath()).isEqualTo("C:\\Program Files\\Tesseract-OCR");
    }

    @Test
    void shouldBindTessdataPath() {
        assertThat(config.tessdataPath()).isEqualTo("C:\\Program Files\\Tesseract-OCR\\tessdata");
    }
}
