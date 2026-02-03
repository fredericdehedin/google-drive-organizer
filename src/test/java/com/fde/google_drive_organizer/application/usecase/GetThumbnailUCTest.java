package com.fde.google_drive_organizer.application.usecase;

import com.fde.google_drive_organizer.domain.port.outbound.ThumbnailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetThumbnailUCTest {

    @Mock
    private ThumbnailRepository thumbnailRepository;

    @InjectMocks
    private GetThumbnailUC getThumbnailUC;

    @Test
    void shouldReturnThumbnailWhenFound() {
        String fileId = "test-file-id";
        byte[] expectedThumbnail = new byte[]{1, 2, 3};
        when(thumbnailRepository.getThumbnail(fileId)).thenReturn(Optional.of(expectedThumbnail));

        Optional<byte[]> result = getThumbnailUC.execute(fileId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedThumbnail);
        verify(thumbnailRepository).getThumbnail(fileId);
    }

    @Test
    void shouldReturnEmptyWhenThumbnailNotFound() {
        String fileId = "non-existent-file-id";
        when(thumbnailRepository.getThumbnail(fileId)).thenReturn(Optional.empty());

        Optional<byte[]> result = getThumbnailUC.execute(fileId);

        assertThat(result).isEmpty();
        verify(thumbnailRepository).getThumbnail(fileId);
    }
}
