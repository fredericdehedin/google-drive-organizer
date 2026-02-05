package com.fde.google_drive_organizer.adapter.outbound.drive;

import com.fde.google_drive_organizer.adapter.outbound.cache.DiskThumbnailCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleDriveThumbnailRepositoryTest {

    @Mock
    private DiskThumbnailCache cache;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    private GoogleDriveThumbnailRepository repository;

    @BeforeEach
    void setUp() {
        repository = new GoogleDriveThumbnailRepository(cache, accessTokenProvider);
    }

    @Test
    void shouldCheckCacheForThumbnail() {
        String fileId = "test-file-id";
        byte[] cachedData = new byte[]{1, 2, 3};
        when(cache.get(fileId)).thenReturn(Optional.of(cachedData));

        Optional<byte[]> result = repository.getThumbnail(fileId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(cachedData);
        verify(cache).get(fileId);
        verifyNoInteractions(accessTokenProvider);
    }
}
