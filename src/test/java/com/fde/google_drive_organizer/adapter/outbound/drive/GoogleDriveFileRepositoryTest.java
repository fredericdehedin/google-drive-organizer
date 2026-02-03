package com.fde.google_drive_organizer.adapter.outbound.drive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleDriveFileRepositoryTest {

    private static final String CHECK_IN_FOLDER_ID = "test-folder-id";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    private GoogleDriveFileRepository repository;

    @BeforeEach
    void setUp() {
        DriveConfig config = new DriveConfig(CHECK_IN_FOLDER_ID);
        repository = new GoogleDriveFileRepository(accessTokenProvider, config);
    }

    @Test
    void shouldThrowExceptionWhenAccessTokenIsNull() {
        when(accessTokenProvider.getAccessToken()).thenReturn(null);

        assertThatThrownBy(() -> repository.getFilesInCheckInFolder())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No access token available");
    }

    @Test
    void shouldThrowExceptionWhenAccessTokenIsNotAvailable() {
        when(accessTokenProvider.getAccessToken()).thenReturn(null);

        assertThatThrownBy(() -> repository.getFilesInCheckInFolder())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No access token available");
    }
}
