package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.domain.model.DriveFile;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleDriveFileRepositoryTest {

    private static final String CHECK_IN_FOLDER_ID = "test-folder-id";

    @Mock
    private Drive drive;

    private GoogleDriveFileRepository repository;

    @BeforeEach
    void setUp() {
        DriveConfig config = new DriveConfig(CHECK_IN_FOLDER_ID);
        repository = new GoogleDriveFileRepository(drive, config);
    }

    @Test
    void shouldReturnFilesFromCheckInFolder() throws IOException {
        Drive.Files files = mock(Drive.Files.class);
        Drive.Files.List listRequest = mock(Drive.Files.List.class);
        FileList fileList = new FileList();
        
        File file1 = new File()
                .setId("file1")
                .setName("Document.pdf")
                .setMimeType("application/pdf")
                .setIconLink("https://icon1.png")
                .setThumbnailLink("https://thumb1.png");
        
        File file2 = new File()
                .setId("file2")
                .setName("Image.jpg")
                .setMimeType("image/jpeg")
                .setIconLink("https://icon2.png")
                .setThumbnailLink("https://thumb2.png");
        
        fileList.setFiles(List.of(file1, file2));

        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(listRequest);
        when(listRequest.setQ("'test-folder-id' in parents and trashed=false and mimeType!='application/vnd.google-apps.folder'")).thenReturn(listRequest);
        when(listRequest.setFields("files(id, name, mimeType, iconLink, thumbnailLink)")).thenReturn(listRequest);
        when(listRequest.setPageSize(30)).thenReturn(listRequest);
        when(listRequest.execute()).thenReturn(fileList);

        List<DriveFile> result = repository.getFilesInCheckInFolder();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo("file1");
        assertThat(result.get(0).name()).isEqualTo("Document.pdf");
        assertThat(result.get(1).id()).isEqualTo("file2");
        assertThat(result.get(1).name()).isEqualTo("Image.jpg");
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDriveApiCallFails() throws IOException {
        Drive.Files files = mock(Drive.Files.class);
        Drive.Files.List listRequest = mock(Drive.Files.List.class);

        when(drive.files()).thenReturn(files);
        when(files.list()).thenReturn(listRequest);
        when(listRequest.setQ("'test-folder-id' in parents and trashed=false and mimeType!='application/vnd.google-apps.folder'")).thenReturn(listRequest);
        when(listRequest.setFields("files(id, name, mimeType, iconLink, thumbnailLink)")).thenReturn(listRequest);
        when(listRequest.setPageSize(30)).thenReturn(listRequest);
        when(listRequest.execute()).thenThrow(new IOException("API error"));

        assertThatThrownBy(() -> repository.getFilesInCheckInFolder())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to list files from Google Drive")
                .hasCauseInstanceOf(IOException.class);
    }
}
