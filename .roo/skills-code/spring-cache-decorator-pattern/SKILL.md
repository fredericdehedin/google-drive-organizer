---
name: spring-cache-decorator-pattern
description: Implement Spring Cache using Decorator pattern with @Primary, @Cacheable, and @ContextConfiguration testing. Use when caching is explicitly requested or when adding caching to existing repositories/services.
---

# Spring Cache Decorator Pattern

## When to Use

- **Explicitly requested**: When the user asks to add caching to a repository or service
- **Caching requested**: When caching is mentioned, implement it using this Decorator approach
- **Never mix**: Do not add caching logic directly to business logic classes

## Implementation

### 1. Delegate (Keep Unchanged)

```java
@Repository
public class GoogleDriveThumbnailRepository implements ThumbnailRepository {
    @Override
    public byte[] getThumbnail(String fileId) {
        // Pure business logic - no caching
    }
}
```

### 2. Decorator with @Primary and @Cacheable

```java
@Repository
@Primary
public class ThumbnailCacheableRepository implements ThumbnailRepository {
    
    public static final String THUMBNAIL_CACHE_NAME = "thumbnails";
    private final ThumbnailRepository thumbnailRepositoryDelegate;
    
    public ThumbnailCacheableRepository(ThumbnailRepository thumbnailRepositoryDelegate) {
        this.thumbnailRepositoryDelegate = thumbnailRepositoryDelegate;
    }
    
    @Override
    @Cacheable(value = THUMBNAIL_CACHE_NAME)
    public byte[] getThumbnail(String fileId) {
        return thumbnailRepositoryDelegate.getThumbnail(fileId);
    }
}
```

### 4. Test with @ContextConfiguration

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CacheableRepositoryTest.TestConfig.class)
class CacheableRepositoryTest {
    
    @Autowired
    private ThumbnailRepository cacheableRepository;
    
    @Autowired
    @Qualifier("mockDelegate")
    private ThumbnailRepository mockDelegate;
    
    //Actual tests and assertions
    
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
        public ThumbnailRepository cacheableRepository(
                @Qualifier("mockDelegate") ThumbnailRepository delegate) {
            return new ThumbnailCacheableRepository(delegate);
        }
    }
}
```

## Key Points

- **@Primary**: Decorator becomes default bean, Spring auto-resolves non-primary delegate
- **@Cacheable**: Spring handles caching automatically
- **@ContextConfiguration**: Minimal Spring context for testing
- **ConcurrentMapCacheManager**: Simple in-memory cache for tests
- **Verify once**: Ensure delegate called only once (cache hit)
