package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.google.api.services.drive.Drive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleDriveConfigurationTest {

    @Mock
    private AccessTokenProvider accessTokenProvider;

    private final GoogleDriveConfiguration configuration = new GoogleDriveConfiguration();

    @Test
    void shouldCreateDriveServiceWithValidAccessToken() {
        when(accessTokenProvider.getAccessToken()).thenReturn("valid-access-token");

        Drive drive = configuration.googleDrive(accessTokenProvider);

        assertThat(drive).isNotNull();
        assertThat(drive.getApplicationName()).isEqualTo(GoogleDriveConfiguration.APPLICATION_NAME);
    }

    @Test
    void shouldThrowExceptionWhenAccessTokenIsNull() {
        when(accessTokenProvider.getAccessToken()).thenReturn(null);

        assertThatThrownBy(() -> configuration.googleDrive(accessTokenProvider))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No access token available");
    }
}
