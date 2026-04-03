package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.adapter.outbound.tika.DocumentParser;
import com.fde.google_drive_organizer.domain.drive_file.DriveFileId;
import com.fde.google_drive_organizer.domain.exception.DocumentContentExtractionException;
import com.fde.google_drive_organizer.domain.model.DocumentContent;
import com.fde.google_drive_organizer.domain.suggest_target_folder_progress.SuggestTargetFolderProgressPublisher;
import com.fde.google_drive_organizer.progress.ProgressStep;
import com.google.api.services.drive.Drive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private SuggestTargetFolderProgressPublisher publisher;

    private GoogleDriveDocumentContentRepository repository;

    @BeforeEach
    void setUp() {
        when(driveProvider.getObject()).thenReturn(drive);
        repository = new GoogleDriveDocumentContentRepository(driveProvider, textParser, ocrParser, publisher);
    }

    @Test
    void shouldExtractContentUsingTextParserWhenTextIsFound() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);
        when(drive.files()).thenReturn(files);
        when(files.get(FILE_ID)).thenReturn(get);
        when(get.executeMediaAsInputStream()).thenReturn(inputStream);
        when(textParser.parseToText(any(InputStream.class))).thenReturn("Extracted text content");

        DocumentContent result = repository.extractContent(FILE_ID);

        assertThat(result.fileId()).isEqualTo(FILE_ID);
        assertThat(result.textContent()).isEqualTo("Extracted text content");
        verifyNoInteractions(ocrParser);
        verify(publisher).publish(new DriveFileId(FILE_ID), ProgressStep.DOWNLOADING, "Downloading file...");
        verify(publisher).publish(new DriveFileId(FILE_ID), ProgressStep.EXTRACTING_TEXT, "Extracting text...");
    }

    @Test
    void shouldFallbackToOcrWhenTextParserReturnsEmptyString() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);
        when(drive.files()).thenReturn(files);
        when(files.get(FILE_ID)).thenReturn(get);
        when(get.executeMediaAsInputStream()).thenReturn(inputStream);
        when(textParser.parseToText(any(InputStream.class))).thenReturn("");
        when(ocrParser.parseToText(any(InputStream.class))).thenReturn("OCR extracted text");

        DocumentContent result = repository.extractContent(FILE_ID);

        assertThat(result.fileId()).isEqualTo(FILE_ID);
        assertThat(result.textContent()).isEqualTo("OCR extracted text");
        verify(publisher).publish(new DriveFileId(FILE_ID), ProgressStep.OCR, "Running OCR...");
    }

    @Test
    void shouldFallbackToOcrWhenTextParserReturnsBlankString() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);
        when(drive.files()).thenReturn(files);
        when(files.get(FILE_ID)).thenReturn(get);
        when(get.executeMediaAsInputStream()).thenReturn(inputStream);
        when(textParser.parseToText(any(InputStream.class))).thenReturn("   ");
        when(ocrParser.parseToText(any(InputStream.class))).thenReturn("OCR extracted text");

        DocumentContent result = repository.extractContent(FILE_ID);

        assertThat(result.fileId()).isEqualTo(FILE_ID);
        assertThat(result.textContent()).isEqualTo("OCR extracted text");
        verify(publisher).publish(new DriveFileId(FILE_ID), ProgressStep.OCR, "Running OCR...");
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
        when(textParser.parseToText(any(InputStream.class)))
                .thenThrow(new DocumentContentExtractionException("Text extraction failed", new RuntimeException("Parse error")));

        assertThatThrownBy(() -> repository.extractContent(FILE_ID))
                .isInstanceOf(DocumentContentExtractionException.class)
                .hasMessage("Text extraction failed")
                .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldThrowExceptionWhenOcrParserFails() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(FILE_CONTENT);
        when(drive.files()).thenReturn(files);
        when(files.get(FILE_ID)).thenReturn(get);
        when(get.executeMediaAsInputStream()).thenReturn(inputStream);
        when(textParser.parseToText(any(InputStream.class))).thenReturn("");
        when(ocrParser.parseToText(any(InputStream.class)))
                .thenThrow(new DocumentContentExtractionException("OCR extraction failed", new RuntimeException("OCR error")));

        assertThatThrownBy(() -> repository.extractContent(FILE_ID))
                .isInstanceOf(DocumentContentExtractionException.class)
                .hasMessage("OCR extraction failed")
                .hasCauseInstanceOf(RuntimeException.class);
    }
}
