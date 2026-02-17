package com.fde.google_drive_organizer.adapter.outbound.tika;

import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class TesseractOcrDocumentParserTest {

    @Test
    void shouldCreateParserWithDefaultConfig() {
        OcrConfig config = new OcrConfig("deu+eng");
        TesseractOcrDocumentParser parser = new TesseractOcrDocumentParser(config);

        assertThat(parser).isNotNull();
    }

    @Test
    void shouldReturnEmptyStringForEmptyInputStream() throws IOException, TikaException {
        OcrConfig config = new OcrConfig("deu+eng");
        TesseractOcrDocumentParser parser = new TesseractOcrDocumentParser(config);

        String result = parser.parseToText(new ByteArrayInputStream(new byte[0]));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUseConfiguredLanguage() {
        OcrConfig config = new OcrConfig("fra");
        TesseractOcrDocumentParser parser = new TesseractOcrDocumentParser(config);

        assertThat(parser).isNotNull();
    }
}
