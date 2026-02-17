package com.fde.google_drive_organizer.adapter.outbound.tika;

import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class TikaPdfTextDocumentParserTest {

    private final TikaPdfTextDocumentParser tikaPdfTextDocumentParser = new TikaPdfTextDocumentParser();

    @Test
    void shouldParseTextFromPlainTextInputStream() throws IOException, TikaException {
        String content = "Hello, this is plain text content.";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        String result = tikaPdfTextDocumentParser.parseToText(inputStream);

        assertThat(result).contains("Hello, this is plain text content.");
    }

    @Test
    void shouldReturnEmptyStringForEmptyInputStream() throws IOException, TikaException {
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        String result = tikaPdfTextDocumentParser.parseToText(inputStream);

        assertThat(result).isEmpty();
    }
}
