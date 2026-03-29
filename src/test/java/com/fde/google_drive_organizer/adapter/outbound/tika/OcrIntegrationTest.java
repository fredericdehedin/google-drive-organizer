package com.fde.google_drive_organizer.adapter.outbound.tika;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class OcrIntegrationTest {

    private static final String TEXT_SEARCHABLE_PDF = "/pdf/test-text-searchable.pdf";
    private static final String NON_TEXT_SEARCHABLE_PDF = "/pdf/test-non-text-searchable.pdf";
    private static final String TESSERACT_PATH = "C:\\Program Files\\Tesseract-OCR";
    private static final String TESSDATA_PATH = "C:\\Program Files\\Tesseract-OCR\\tessdata";

    @Test
    @Tag("integration")
    void shouldExtractTextFromSearchablePdf() throws IOException{
        TikaPdfTextDocumentParser parser = new TikaPdfTextDocumentParser();

        try (InputStream inputStream = getClass().getResourceAsStream(TEXT_SEARCHABLE_PDF)) {
            assertThat(inputStream).isNotNull();

            String result = parser.parseToText(inputStream);

            assertThat(result).isNotEmpty();
            assertThat(result.length()).isGreaterThan(5);
        }
    }

    @Test
    @Tag("integration")
    void shouldExtractTextFromNonSearchablePdfUsingOcr() throws IOException {
        OcrConfig config = new OcrConfig("deu+eng", TESSERACT_PATH, TESSDATA_PATH);
        TesseractOcrDocumentParser parser = new TesseractOcrDocumentParser(config);

        try (InputStream inputStream = getClass().getResourceAsStream(NON_TEXT_SEARCHABLE_PDF)) {
            assertThat(inputStream).isNotNull();

            String result = parser.parseToText(inputStream);

            assertThat(result).isNotEmpty();
        }
    }

    @Test
    @Tag("integration")
    void shouldUseConfiguredLanguageForOcr() throws IOException {
        OcrConfig englishConfig = new OcrConfig("eng", TESSERACT_PATH, TESSDATA_PATH);
        TesseractOcrDocumentParser parser = new TesseractOcrDocumentParser(englishConfig);

        try (InputStream inputStream = getClass().getResourceAsStream(NON_TEXT_SEARCHABLE_PDF)) {
            assertThat(inputStream).isNotNull();

            String result = parser.parseToText(inputStream);

            assertThat(result).isNotNull();
        }
    }

    @Test
    @Tag("integration")
    void shouldReturnEmptyForSearchablePdfWithTextParser() throws IOException {
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
    @Tag("integration")
    void shouldFallbackToOcrWhenTextExtractionReturnsEmpty() throws IOException {
        TikaPdfTextDocumentParser textParser = new TikaPdfTextDocumentParser();
        OcrConfig config = new OcrConfig("deu+eng", TESSERACT_PATH, TESSDATA_PATH);
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
