package com.fde.google_drive_organizer.adapter.outbound.cache;

import com.fde.google_drive_organizer.domain.port.outbound.ThumbnailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.fde.google_drive_organizer.adapter.outbound.cache.ThumbnailCacheConfiguration.THUMBNAIL_CACHE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ThumbnailCacheableRepositoryTest.TestConfig.class)
class ThumbnailCacheableRepositoryTest {

    @Autowired
    private ThumbnailRepository cacheableRepository;

    @Autowired
    @Qualifier("mockDelegate")
    private ThumbnailRepository mockDelegate;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        // Clear cache before each test
        cacheManager.getCache(THUMBNAIL_CACHE_NAME).clear();
        reset(mockDelegate);
    }

    @Test
    void shouldCacheThumbnailOnFirstCall() {
        String fileId = "test-file-id";
        byte[] thumbnailData = new byte[]{1, 2, 3, 4, 5};
        when(mockDelegate.getThumbnail(fileId)).thenReturn(thumbnailData);

        byte[] result = cacheableRepository.getThumbnail(fileId);

        assertThat(result).isEqualTo(thumbnailData);
        verify(mockDelegate, times(1)).getThumbnail(fileId);
    }

    @Test
    void shouldReturnCachedThumbnailOnSecondCall() {
        String fileId = "test-file-id";
        byte[] thumbnailData = new byte[]{1, 2, 3, 4, 5};
        when(mockDelegate.getThumbnail(fileId)).thenReturn(thumbnailData);

        // First call - should hit delegate
        byte[] firstResult = cacheableRepository.getThumbnail(fileId);
        assertThat(firstResult).isEqualTo(thumbnailData);

        // Second call - should hit cache
        byte[] secondResult = cacheableRepository.getThumbnail(fileId);
        assertThat(secondResult).isEqualTo(thumbnailData);

        // Verify delegate was only called once
        verify(mockDelegate, times(1)).getThumbnail(fileId);
    }

    @Test
    void shouldCacheNullValues() {
        String fileId = "file-without-thumbnail";
        when(mockDelegate.getThumbnail(fileId)).thenReturn(null);

        // First call - should hit delegate
        byte[] firstResult = cacheableRepository.getThumbnail(fileId);
        assertThat(firstResult).isNull();

        // Second call - should hit cache
        byte[] secondResult = cacheableRepository.getThumbnail(fileId);
        assertThat(secondResult).isNull();

        // Verify delegate was only called once
        verify(mockDelegate, times(1)).getThumbnail(fileId);
    }

    @Test
    void shouldCacheDifferentFilesSeparately() {
        String fileId1 = "file-1";
        String fileId2 = "file-2";
        byte[] thumbnail1 = new byte[]{1, 2, 3};
        byte[] thumbnail2 = new byte[]{4, 5, 6};

        when(mockDelegate.getThumbnail(fileId1)).thenReturn(thumbnail1);
        when(mockDelegate.getThumbnail(fileId2)).thenReturn(thumbnail2);

        // Call for file 1
        byte[] result1 = cacheableRepository.getThumbnail(fileId1);
        assertThat(result1).isEqualTo(thumbnail1);

        // Call for file 2
        byte[] result2 = cacheableRepository.getThumbnail(fileId2);
        assertThat(result2).isEqualTo(thumbnail2);

        // Call for file 1 again - should hit cache
        byte[] result1Again = cacheableRepository.getThumbnail(fileId1);
        assertThat(result1Again).isEqualTo(thumbnail1);

        // Verify each delegate was called exactly once
        verify(mockDelegate, times(1)).getThumbnail(fileId1);
        verify(mockDelegate, times(1)).getThumbnail(fileId2);
    }

    @Test
    void shouldPropagateExceptionsFromDelegate() {
        String fileId = "error-file";
        RuntimeException expectedException = new RuntimeException("Failed to fetch thumbnail");
        when(mockDelegate.getThumbnail(fileId)).thenThrow(expectedException);

        try {
            cacheableRepository.getThumbnail(fileId);
        } catch (RuntimeException e) {
            assertThat(e).isEqualTo(expectedException);
        }

        // Verify exception was not cached - second call should also throw
        try {
            cacheableRepository.getThumbnail(fileId);
        } catch (RuntimeException e) {
            assertThat(e).isEqualTo(expectedException);
        }

        // Verify delegate was called twice (exceptions are not cached)
        verify(mockDelegate, times(2)).getThumbnail(fileId);
    }

    @Configuration
    @EnableCaching
    static class TestConfig {

        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(THUMBNAIL_CACHE_NAME);
        }

        @Bean
        @Qualifier("mockDelegate")
        public ThumbnailRepository mockDelegate() {
            return mock(ThumbnailRepository.class);
        }

        @Bean
        public ThumbnailRepository cacheableRepository(@Qualifier("mockDelegate") ThumbnailRepository delegate) {
            return new ThumbnailCacheableRepository(delegate);
        }
    }
}
