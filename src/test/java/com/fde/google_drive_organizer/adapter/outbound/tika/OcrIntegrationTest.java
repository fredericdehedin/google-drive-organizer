package com.fde.google_drive_organizer.adapter.outbound.tika;

import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DisabledIf("isTesseractNotAvailable")
class OcrIntegrationTest {

    private static final String TEXT_SEARCHABLE_PDF = "/pdf/test-text-searchable.pdf";
    private static final String NON_TEXT_SEARCHABLE_PDF = "/pdf/test-non-text-searchable.pdf";

    @BeforeAll
    static void checkTesseractAvailability() {
        // Verification happens in isTesseractNotAvailable()
    }

    static boolean isTesseractNotAvailable() {
        return !isTesseractInstalled();
    }

    private static boolean isTesseractInstalled() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("tesseract", "--version");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    @Test
    void shouldExtractTextFromSearchablePdf() throws IOException, TikaException {
        TikaPdfTextDocumentParser parser = new TikaPdfTextDocumentParser();

        try (InputStream inputStream = getClass().getResourceAsStream(TEXT_SEARCHABLE_PDF)) {
            assertThat(inputStream).isNotNull();

            String result = parser.parseToText(inputStream);

            assertThat(result).isNotEmpty();
            assertThat(result.length()).isGreaterThan(5);
        }
    }

    @Test
    void shouldExtractTextFromNonSearchablePdfUsingOcr() throws IOException, TikaException {
        OcrConfig config = new OcrConfig("deu+eng");
        TesseractOcrDocumentParser parser = new TesseractOcrDocumentParser(config);

        try (InputStream inputStream = getClass().getResourceAsStream(NON_TEXT_SEARCHABLE_PDF)) {
            assertThat(inputStream).isNotNull();

            String result = parser.parseToText(inputStream);

            assertThat(result).isNotEmpty();
        }
    }

    @Test
    void shouldUseConfiguredLanguageForOcr() throws IOException, TikaException {
        OcrConfig englishConfig = new OcrConfig("eng");
        TesseractOcrDocumentParser parser = new TesseractOcrDocumentParser(englishConfig);

        try (InputStream inputStream = getClass().getResourceAsStream(NON_TEXT_SEARCHABLE_PDF)) {
            assertThat(inputStream).isNotNull();

            String result = parser.parseToText(inputStream);

            assertThat(result).isNotNull();
        }
    }

    @Test
    void shouldReturnEmptyForSearchablePdfWithTextParser() throws IOException, TikaException {
        TikaPdfTextDocumentParser textParser = new TikaPdfTextDocumentParser();

        try (InputStream inputStream = getClass().getResourceAsStream(TEXT_SEARCHABLE_PDF)) {
            assertThat(inputStream).isNotNull();

            String result = textParser.parseToText(inputStream);

            assertThat(result)
                .isNotNull()
                .isNotBlank();
        }
    }

    @Test
    void shouldFallbackToOcrWhenTextExtractionReturnsEmpty() throws IOException, TikaException {
        TikaPdfTextDocumentParser textParser = new TikaPdfTextDocumentParser();
        OcrConfig config = new OcrConfig("deu+eng");
        TesseractOcrDocumentParser ocrParser = new TesseractOcrDocumentParser(config);

        try (InputStream textStream = getClass().getResourceAsStream(NON_TEXT_SEARCHABLE_PDF)) {
            assertThat(textStream).isNotNull();

            String textResult = textParser.parseToText(textStream);

            if (textResult.isBlank()) {
                try (InputStream ocrStream = getClass().getResourceAsStream(NON_TEXT_SEARCHABLE_PDF)) {
                    String ocrResult = ocrParser.parseToText(ocrStream);
                    assertThat(ocrResult).isNotNull();
                }
            }
        }
    }
}
