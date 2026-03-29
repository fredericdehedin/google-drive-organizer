package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.google.api.services.drive.Drive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleDriveThumbnailRepositoryTest {

    @Mock
    private ObjectProvider<Drive> driveProvider;

    @Mock
    private Drive drive;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    private GoogleDriveThumbnailRepository repository;

    @BeforeEach
    void setUp() {
        repository = new GoogleDriveThumbnailRepository(driveProvider, accessTokenProvider);
    }

    @Test
    void shouldReturnNullWhenAccessTokenProviderReturnsNull() {
        when(accessTokenProvider.getAccessToken()).thenReturn(null);

        byte[] result = repository.getThumbnail("test-file-id");

        assertThat(result).isNull();
    }

    @Test
    void shouldThrowExceptionWhenAccessTokenProviderThrowsException() {
        when(accessTokenProvider.getAccessToken()).thenThrow(new RuntimeException("Token error"));

        assertThatThrownBy(() -> repository.getThumbnail("test-file-id"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Token error");
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDriveApiCallFails() throws IOException {
        when(accessTokenProvider.getAccessToken()).thenReturn("valid-token");
        when(driveProvider.getObject()).thenReturn(drive);
        
        Drive.Files files = mock(Drive.Files.class);
        Drive.Files.Get getRequest = mock(Drive.Files.Get.class);

        when(drive.files()).thenReturn(files);
        when(files.get(anyString())).thenReturn(getRequest);
        when(getRequest.setFields(anyString())).thenReturn(getRequest);
        when(getRequest.execute()).thenThrow(new IOException("API error"));

        assertThatThrownBy(() -> repository.getThumbnail("test-file-id"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to fetch thumbnail from Google Drive for fileId: test-file-id")
                .hasCauseInstanceOf(IOException.class);
    }
}
