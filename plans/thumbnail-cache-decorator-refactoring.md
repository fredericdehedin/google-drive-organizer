# Thumbnail Cache Decorator Refactoring Plan

## Overview

Refactor the thumbnail caching architecture to use a **Decorator Pattern** with **Spring Cache** support. This separates concerns by having [`GoogleDriveThumbnailRepository`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveThumbnailRepository.java) focus solely on Google Drive API interactions, while a new [`GoogleDriveThumbnailCacheableRepository`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/GoogleDriveThumbnailCacheableRepository.java) handles caching logic.

## Current Architecture Issues

1. **Mixed Responsibilities**: [`GoogleDriveThumbnailRepository`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveThumbnailRepository.java) handles both Google Drive API calls AND caching logic
2. **Tight Coupling**: Direct dependency on [`DiskThumbnailCache`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/DiskThumbnailCache.java)
3. **Limited Flexibility**: Cannot easily switch caching strategies or disable caching

## Target Architecture

```mermaid
graph TD
    A[GetThumbnailUC] -->|uses| B[ThumbnailRepository interface]
    B -->|implemented by| C[GoogleDriveThumbnailCacheableRepository]
    C -->|@Primary bean| B
    C -->|delegates to| D[GoogleDriveThumbnailRepository]
    C -->|uses @Cacheable| E[Spring Cache]
    D -->|calls| F[Google Drive API]
    E -->|backed by| G[ConcurrentMapCacheManager or Custom]
```

## Design Decisions

### Cache Strategy
**Decision**: Keep disk-based caching using [`DiskThumbnailCache`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/DiskThumbnailCache.java) wrapped in Spring Cache abstraction for persistent storage.

### Eviction Policy
**Decision**: No eviction policy for now - cache grows indefinitely on disk.

### Return Type: `Optional<byte[]>` vs `byte[]`

**Decision**: **Change to `byte[]` (nullable) for the interface** ([`ThumbnailRepository.getThumbnail()`](src/main/java/com/fde/google_drive_organizer/domain/port/outbound/ThumbnailRepository.java:7))

**Rationale**:
1. ✅ **Simpler semantics** - `byte[]` as an array can implicitly be empty or null, requiring explicit handling
2. ✅ **Exception-based error handling** - Errors (IO errors, file not found, etc.) should throw exceptions, not return empty Optional
3. ✅ **Clearer intent** - `null` return explicitly means "no thumbnail available" (valid business case)
4. ✅ **Future exception handler** - Will implement centralized exception handling for error cases
5. ✅ **Aligns with exception strategy** - Separates "no data" (null) from "error occurred" (exception)

**Error Handling Strategy**:
- **`null` return**: File exists but has no thumbnail (valid business case)
- **Exception thrown**: IO error, authentication failure, file not found, etc. (error cases)
- **Future work**: Implement `@ControllerAdvice` exception handler for centralized error handling

**Impact**: Requires updating 7+ files:
- [`ThumbnailRepository`](src/main/java/com/fde/google_drive_organizer/domain/port/outbound/ThumbnailRepository.java) interface
- [`GoogleDriveThumbnailRepository`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveThumbnailRepository.java) implementation
- New `GoogleDriveThumbnailCacheableRepository` decorator
- [`GetThumbnailUC`](src/main/java/com/fde/google_drive_organizer/application/usecase/GetThumbnailUC.java) use case
- [`ThumbnailController`](src/main/java/com/fde/google_drive_organizer/adapter/inbound/http/ThumbnailController.java) controller
- [`GetThumbnailUCTest`](src/test/java/com/fde/google_drive_organizer/application/usecase/GetThumbnailUCTest.java) test
- [`ThumbnailControllerTest`](src/test/java/com/fde/google_drive_organizer/adapter/inbound/http/ThumbnailControllerTest.java) test

**New Controller Pattern**:
```java
// ThumbnailController.java - will change to:
byte[] thumbnail = getThumbnailUC.execute(fileId);
if (thumbnail == null) {
    return ResponseEntity.notFound().build();
}
return ResponseEntity.ok()
    .contentType(MediaType.IMAGE_JPEG)
    .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
    .body(thumbnail);
```

### Method Signature Refactoring
**Decision**: All methods in the thumbnail retrieval chain will use `byte[]` (nullable) consistently.

**Rationale**: Consistent return type throughout the call chain simplifies the implementation and aligns with the exception-based error handling strategy.

## Implementation Steps

### 1. Refactor GoogleDriveThumbnailRepository

**File**: [`src/main/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveThumbnailRepository.java`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveThumbnailRepository.java)

**Changes**:
- Remove [`DiskThumbnailCache`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/DiskThumbnailCache.java) dependency
- Remove all caching logic (cache.get(), cache.put())
- Focus only on fetching thumbnails from Google Drive API
- Remove `@Component` annotation (will be injected as dependency)
- Rename `fetchFromGoogleDrive()` to just implement `getThumbnail()` directly

**Before**:
```java
@Component
public class GoogleDriveThumbnailRepository implements ThumbnailRepository {
    private final DiskThumbnailCache cache;
    private final AccessTokenProvider accessTokenProvider;
    
    @Override
    public Optional<byte[]> getThumbnail(String fileId) {
        Optional<byte[]> cachedThumbnail = cache.get(fileId);
        if (cachedThumbnail.isPresent()) {
            return cachedThumbnail;
        }
        return fetchFromGoogleDrive(fileId);
    }
}
```

**After**:
```java
public class GoogleDriveThumbnailRepository implements ThumbnailRepository {
    private final AccessTokenProvider accessTokenProvider;
    
    @Override
    public Optional<byte[]> getThumbnail(String fileId) {
        try {
            byte[] thumbnail = fetchThumbnailFromGoogleDrive(fileId);
            return Optional.ofNullable(thumbnail);
        } catch (Exception e) {
            log.error("Failed to fetch thumbnail for fileId: {}", fileId, e);
            return Optional.empty();
        }
    }
    
    private byte[] fetchThumbnailFromGoogleDrive(String fileId) {
        // Returns byte[] or null if no thumbnail available
        // Throws exceptions for actual errors
    }
}
```

### 2. Create GoogleDriveThumbnailCacheableRepository

**File**: [`src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/GoogleDriveThumbnailCacheableRepository.java`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/GoogleDriveThumbnailCacheableRepository.java)

**Purpose**: Decorator that adds caching behavior using Spring Cache

**Implementation**:
```java
@Component
@Primary
public class GoogleDriveThumbnailCacheableRepository implements ThumbnailRepository {
    
    private final ThumbnailRepository delegate;
    
    public GoogleDriveThumbnailCacheableRepository(
            @Qualifier("googleDriveThumbnailRepository") ThumbnailRepository delegate) {
        this.delegate = delegate;
    }
    
    @Override
    @Cacheable(value = "thumbnails", key = "#fileId")
    public Optional<byte[]> getThumbnail(String fileId) {
        return delegate.getThumbnail(fileId);
    }
}
```

**Key Features**:
- `@Primary`: Makes this the default implementation injected into [`GetThumbnailUC`](src/main/java/com/fde/google_drive_organizer/application/usecase/GetThumbnailUC.java)
- `@Cacheable`: Spring Cache annotation for automatic caching
- `@Qualifier`: Explicitly inject the Google Drive implementation
- Delegates to underlying repository when cache misses

### 3. Configure Spring Cache

**File**: [`src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/ThumbnailCacheConfiguration.java`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/ThumbnailCacheConfiguration.java)

**Purpose**: Configure Spring Cache infrastructure with disk-based caching

**Implementation**:
```java
@Configuration
@EnableCaching
public class ThumbnailCacheConfiguration {
    
    @Bean
    public CacheManager cacheManager(DiskThumbnailCache diskCache) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(
            new DiskThumbnailCacheAdapter("thumbnails", diskCache)
        ));
        return cacheManager;
    }
    
    @Bean("googleDriveThumbnailRepository")
    public ThumbnailRepository googleDriveThumbnailRepository(
            AccessTokenProvider accessTokenProvider) {
        return new GoogleDriveThumbnailRepository(accessTokenProvider);
    }
    
    /**
     * Adapter to integrate DiskThumbnailCache with Spring Cache abstraction
     */
    static class DiskThumbnailCacheAdapter extends AbstractValueAdaptingCache {
        private final DiskThumbnailCache diskCache;
        
        protected DiskThumbnailCacheAdapter(String name, DiskThumbnailCache diskCache) {
            super(true); // allowNullValues = true
            this.diskCache = diskCache;
        }
        
        @Override
        protected Object lookup(Object key) {
            return diskCache.get((String) key).orElse(null);
        }
        
        @Override
        public String getName() {
            return "thumbnails";
        }
        
        @Override
        public Object getNativeCache() {
            return diskCache;
        }
        
        @Override
        public void put(Object key, Object value) {
            if (value != null) {
                diskCache.put((String) key, (byte[]) value);
            }
        }
        
        @Override
        public void evict(Object key) {
            // Not implemented - no eviction for now
        }
        
        @Override
        public void clear() {
            // Not implemented - no clear for now
        }
    }
}
```

**Key Features**:
- Uses existing [`DiskThumbnailCache`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/DiskThumbnailCache.java) for persistent storage
- `DiskThumbnailCacheAdapter` bridges Spring Cache API with disk cache
- No eviction policy implemented

### 4. Update Configuration Properties

**File**: [`src/main/resources/application.yaml`](src/main/resources/application.yaml)

**Add Cache Configuration**:
```yaml
spring:
  cache:
    type: simple  # Using custom SimpleCacheManager with DiskThumbnailCache
    cache-names:
      - thumbnails

thumbnail:
  cache:
    directory: ./cache/thumbnails
```

### 5. Write Comprehensive Tests

#### Test 1: GoogleDriveThumbnailRepositoryTest (Updated)

**File**: [`src/test/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveThumbnailRepositoryTest.java`](src/test/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveThumbnailRepositoryTest.java)

**Purpose**: Test Google Drive API interaction WITHOUT caching

**Tests**:
- `shouldFetchThumbnailFromGoogleDrive()`: Verify successful fetch returns byte[]
- `shouldReturnEmptyWhenNoThumbnailLink()`: Handle missing thumbnails (returns null → Optional.empty())
- `shouldReturnEmptyOnApiError()`: Handle API errors gracefully (exception → Optional.empty())
- No cache-related tests (moved to cacheable repository tests)

**Key Change**: Internal method now returns `byte[]` (or null), Optional wrapping happens at interface level

#### Test 2: GoogleDriveThumbnailCacheableRepositoryTest

**File**: [`src/test/java/com/fde/google_drive_organizer/adapter/outbound/cache/GoogleDriveThumbnailCacheableRepositoryTest.java`](src/test/java/com/fde/google_drive_organizer/adapter/outbound/cache/GoogleDriveThumbnailCacheableRepositoryTest.java)

**Purpose**: Test caching behavior with Spring Cache

**Approach**: Use `@ContextConfiguration` with minimal Spring context

**Implementation**:
```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    GoogleDriveThumbnailCacheableRepositoryTest.TestConfig.class
})
class GoogleDriveThumbnailCacheableRepositoryTest {
    
    @Configuration
    @EnableCaching
    static class TestConfig {
        @Bean
        public DiskThumbnailCache diskThumbnailCache() {
            // Use temp directory for tests
            ThumbnailCacheConfig config = new ThumbnailCacheConfig(
                System.getProperty("java.io.tmpdir") + "/test-thumbnails"
            );
            return new DiskThumbnailCache(config);
        }
        
        @Bean
        public CacheManager cacheManager(DiskThumbnailCache diskCache) {
            SimpleCacheManager cacheManager = new SimpleCacheManager();
            cacheManager.setCaches(List.of(
                new ThumbnailCacheConfiguration.DiskThumbnailCacheAdapter(
                    "thumbnails", diskCache
                )
            ));
            return cacheManager;
        }
        
        @Bean
        public ThumbnailRepository mockDelegate() {
            return mock(ThumbnailRepository.class);
        }
        
        @Bean
        @Primary
        public GoogleDriveThumbnailCacheableRepository cacheableRepository(
                ThumbnailRepository mockDelegate) {
            return new GoogleDriveThumbnailCacheableRepository(mockDelegate);
        }
    }
    
    @Autowired
    private GoogleDriveThumbnailCacheableRepository repository;
    
    @Autowired
    private ThumbnailRepository mockDelegate;
    
    @Autowired
    private CacheManager cacheManager;
    
    @BeforeEach
    void setUp() {
        // Clear cache before each test
        cacheManager.getCache("thumbnails").clear();
        reset(mockDelegate);
    }
    
    @Test
    void shouldCacheThumbnailOnFirstCall() {
        String fileId = "test-file-id";
        byte[] thumbnailData = new byte[]{1, 2, 3};
        when(mockDelegate.getThumbnail(fileId))
            .thenReturn(Optional.of(thumbnailData));
        
        // First call - should hit delegate
        Optional<byte[]> result1 = repository.getThumbnail(fileId);
        
        assertThat(result1).isPresent();
        assertThat(result1.get()).isEqualTo(thumbnailData);
        verify(mockDelegate, times(1)).getThumbnail(fileId);
    }
    
    @Test
    void shouldReturnCachedThumbnailOnSecondCall() {
        String fileId = "test-file-id";
        byte[] thumbnailData = new byte[]{1, 2, 3};
        when(mockDelegate.getThumbnail(fileId))
            .thenReturn(Optional.of(thumbnailData));
        
        // First call - cache miss
        repository.getThumbnail(fileId);
        
        // Second call - cache hit
        Optional<byte[]> result2 = repository.getThumbnail(fileId);
        
        assertThat(result2).isPresent();
        assertThat(result2.get()).isEqualTo(thumbnailData);
        
        // Delegate should only be called once
        verify(mockDelegate, times(1)).getThumbnail(fileId);
    }
    
    @Test
    void shouldNotCacheEmptyResults() {
        String fileId = "no-thumbnail-file";
        when(mockDelegate.getThumbnail(fileId))
            .thenReturn(Optional.empty());
        
        // First call
        repository.getThumbnail(fileId);
        
        // Second call - should hit delegate again
        repository.getThumbnail(fileId);
        
        // Delegate called twice (empty not cached by default)
        verify(mockDelegate, times(2)).getThumbnail(fileId);
    }
    
    @Test
    void shouldCacheDifferentFilesSeparately() {
        String fileId1 = "file-1";
        String fileId2 = "file-2";
        byte[] data1 = new byte[]{1, 2, 3};
        byte[] data2 = new byte[]{4, 5, 6};
        
        when(mockDelegate.getThumbnail(fileId1))
            .thenReturn(Optional.of(data1));
        when(mockDelegate.getThumbnail(fileId2))
            .thenReturn(Optional.of(data2));
        
        // Cache both
        repository.getThumbnail(fileId1);
        repository.getThumbnail(fileId2);
        
        // Retrieve from cache
        Optional<byte[]> result1 = repository.getThumbnail(fileId1);
        Optional<byte[]> result2 = repository.getThumbnail(fileId2);
        
        assertThat(result1.get()).isEqualTo(data1);
        assertThat(result2.get()).isEqualTo(data2);
        
        // Each delegate called only once
        verify(mockDelegate, times(1)).getThumbnail(fileId1);
        verify(mockDelegate, times(1)).getThumbnail(fileId2);
    }
}
```

**Test Coverage**:
- ✅ Cache miss on first call
- ✅ Cache hit on subsequent calls
- ✅ Delegate called only once per unique fileId
- ✅ Empty results handling
- ✅ Multiple files cached independently

### 6. Update Existing Tests

**Files to Update**:
- [`GetThumbnailUCTest`](src/test/java/com/fde/google_drive_organizer/application/usecase/GetThumbnailUCTest.java): Should still work (depends on interface)
- [`ThumbnailControllerTest`](src/test/java/com/fde/google_drive_organizer/adapter/inbound/http/ThumbnailControllerTest.java): Should still work (depends on use case)

**No changes needed** - these tests depend on the [`ThumbnailRepository`](src/main/java/com/fde/google_drive_organizer/domain/port/outbound/ThumbnailRepository.java) interface, which remains unchanged.

## Migration Strategy

### Phase 1: Create New Components
1. Create [`GoogleDriveThumbnailCacheableRepository`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/GoogleDriveThumbnailCacheableRepository.java)
2. Create [`ThumbnailCacheConfiguration`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/ThumbnailCacheConfiguration.java)
3. Write tests for new components

### Phase 2: Refactor Existing Components
1. Refactor [`GoogleDriveThumbnailRepository`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveThumbnailRepository.java) to remove caching
2. Update [`GoogleDriveThumbnailRepositoryTest`](src/test/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveThumbnailRepositoryTest.java)

### Phase 3: Integration & Cleanup
1. Verify all tests pass
2. Test manually with running application
3. Consider deprecating [`DiskThumbnailCache`](src/main/java/com/fde/google_drive_organizer/adapter/outbound/cache/DiskThumbnailCache.java) (or keep for custom cache manager)

## Benefits

1. **Separation of Concerns**: Google Drive repository focuses on API, cache repository focuses on caching
2. **Flexibility**: Easy to switch cache implementations or disable caching
3. **Testability**: Can test Google Drive logic and caching logic independently
4. **Spring Integration**: Leverage Spring Cache abstraction and management
5. **Maintainability**: Clear responsibilities, easier to understand and modify

## Implementation Notes

### Method Signature Rationale

Changing `fetchFromGoogleDrive` from `Optional<byte[]>` to `byte[]`:
- **Cleaner internal logic**: No need to wrap/unwrap Optional in private methods
- **Single responsibility**: Optional wrapping happens once at the interface boundary
- **Null semantics**: `null` means "no thumbnail available" (not an error)
- **Exception semantics**: Exceptions mean actual errors (API failures, network issues)
- **Better testability**: Easier to test internal logic without Optional ceremony

### Cache Behavior

- **Cache hits**: Return cached thumbnail immediately
- **Cache misses**: Delegate to Google Drive repository, cache result
- **Null values**: Spring Cache with `allowNullValues=true` caches null (no thumbnail)
- **Exceptions**: Not cached, propagated to caller

## Next Steps

After plan approval, switch to **Code mode** to implement the changes.
