package com.fde.google_drive_organizer.adapter.outbound.drive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleDriveThumbnailRepositoryTest {

    @Mock
    private AccessTokenProvider accessTokenProvider;

    private GoogleDriveThumbnailRepository repository;

    @BeforeEach
    void setUp() {
        repository = new GoogleDriveThumbnailRepository(accessTokenProvider);
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
}
