package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.adapter.outbound.tika.DocumentParser;
import com.fde.google_drive_organizer.domain.exception.DocumentContentExtractionException;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.google.api.services.drive.Drive;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleDriveDocumentContentRepositoryTest {

    private static final String FILE_ID = "test-file-id";
    private static final byte[] FILE_CONTENT = "PDF content".getBytes(StandardCharsets.UTF_8);

    @Mock
    private ObjectProvider<Drive> driveProvider;

    @Mock
    private Drive drive;

    @Mock
    private Drive.Files files;

    @Mock
    private Drive.Files.Get get;

    @Mock
    private DocumentParser textParser;

    @Mock
    private DocumentParser ocrParser;

    private GoogleDriveDocumentContentRepository repository;

    @BeforeEach
    void setUp() {
        when(driveProvider.getObject()).thenReturn(drive);
        repository = new GoogleDriveDocumentContentRepository(driveProvider, textParser, ocrParser);
    }

    @Test
    void shouldExtractContentUsingTextParserWhenTextIsFound() throws IOException, TikaException {
        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);
        when(drive.files()).thenReturn(files);
        when(files.get(FILE_ID)).thenReturn(get);
        when(get.executeMediaAsInputStream()).thenReturn(inputStream);

        ArgumentCaptor<InputStream> streamCaptor = ArgumentCaptor.forClass(InputStream.class);
        when(textParser.parseToText(streamCaptor.capture())).thenReturn("Extracted text content");

        DocumentContent result = repository.extractContent(FILE_ID);

        assertThat(result.fileId()).isEqualTo(FILE_ID);
        assertThat(result.textContent()).isEqualTo("Extracted text content");
        verify(textParser).parseToText(streamCaptor.getValue());
        verifyNoInteractions(ocrParser);
    }

    @Test
    void shouldFallbackToOcrWhenTextParserReturnsEmptyString() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);
        when(drive.files()).thenReturn(files);
        when(files.get(FILE_ID)).thenReturn(get);
        when(get.executeMediaAsInputStream()).thenReturn(inputStream);

        ArgumentCaptor<InputStream> textStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<InputStream> ocrStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
        when(textParser.parseToText(textStreamCaptor.capture())).thenReturn("");
        when(ocrParser.parseToText(ocrStreamCaptor.capture())).thenReturn("OCR extracted text");

        DocumentContent result = repository.extractContent(FILE_ID);

        assertThat(result.fileId()).isEqualTo(FILE_ID);
        assertThat(result.textContent()).isEqualTo("OCR extracted text");
        verify(textParser).parseToText(textStreamCaptor.getValue());
        verify(ocrParser).parseToText(ocrStreamCaptor.getValue());
    }

    @Test
    void shouldNotUseOcrWhenTextParserReturnsContent() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);
        when(drive.files()).thenReturn(files);
        when(files.get(FILE_ID)).thenReturn(get);
        when(get.executeMediaAsInputStream()).thenReturn(inputStream);

        ArgumentCaptor<InputStream> streamCaptor = ArgumentCaptor.forClass(InputStream.class);
        when(textParser.parseToText(streamCaptor.capture())).thenReturn("Some text");

        repository.extractContent(FILE_ID);

        verify(textParser).parseToText(streamCaptor.getValue());
        verifyNoInteractions(ocrParser);
    }

    @Test
    void shouldThrowExceptionWhenDriveAccessFails() throws IOException {
        when(drive.files()).thenReturn(files);
        when(files.get(FILE_ID)).thenReturn(get);
        when(get.executeMediaAsInputStream()).thenThrow(new IOException("Drive error"));

        assertThatThrownBy(() -> repository.extractContent(FILE_ID))
                .isInstanceOf(DocumentContentExtractionException.class)
                .hasMessageContaining("Failed to download content from Google Drive")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void shouldThrowExceptionWhenTextParserFails() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);
        when(drive.files()).thenReturn(files);
        when(files.get(FILE_ID)).thenReturn(get);
        when(get.executeMediaAsInputStream()).thenReturn(inputStream);

        ArgumentCaptor<InputStream> streamCaptor = ArgumentCaptor.forClass(InputStream.class);
        when(textParser.parseToText(streamCaptor.capture()))
            .thenThrow(new DocumentContentExtractionException("Text extraction failed", new RuntimeException("Parse error")));

        assertThatThrownBy(() -> repository.extractContent(FILE_ID))
                .isInstanceOf(DocumentContentExtractionException.class)
                .hasMessageContaining("Failed to extract content from Google Drive")
                .hasCauseInstanceOf(DocumentContentExtractionException.class);
    }

    @Test
    void shouldThrowExceptionWhenOcrParserFails() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);
        when(drive.files()).thenReturn(files);
        when(files.get(FILE_ID)).thenReturn(get);
        when(get.executeMediaAsInputStream()).thenReturn(inputStream);

        ArgumentCaptor<InputStream> textStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
        ArgumentCaptor<InputStream> ocrStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
        when(textParser.parseToText(textStreamCaptor.capture())).thenReturn("");
        when(ocrParser.parseToText(ocrStreamCaptor.capture()))
            .thenThrow(new DocumentContentExtractionException("OCR extraction failed", new RuntimeException("OCR error")));

        assertThatThrownBy(() -> repository.extractContent(FILE_ID))
                .isInstanceOf(DocumentContentExtractionException.class)
                .hasMessageContaining("Failed to extract content from Google Drive")
                .hasCauseInstanceOf(DocumentContentExtractionException.class);
    }
}
