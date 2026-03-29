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
public class DclFindClientIdsByRelationshipManagersRepository implements FindClientIdsByRelationshipManagersRepository {
    @Override
    public List<ClientId> find(List<String> relationshipManagerIds) {
        // Pure business logic - no caching
    }
}
```

### 2. Decorator with @Primary and @Cacheable

```java
@Primary
@Repository
class DclFindClientIdsByRelationshipManagersCacheableRepository implements FindClientIdsByRelationshipManagersRepository {

    static final String CLIENT_IDS_BY_RELATIONSHIP_MANAGERS_CACHE_NAME = "clientIdsByRelationshipManagers";

    private static final Logger log = LoggerFactory.getLogger(DclFindClientIdsByRelationshipManagersCacheableRepository.class);

    private final FindClientIdsByRelationshipManagersRepository delegate;

    DclFindClientIdsByRelationshipManagersCacheableRepository(FindClientIdsByRelationshipManagersRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    @Cacheable(value = CLIENT_IDS_BY_RELATIONSHIP_MANAGERS_CACHE_NAME)
    public List<ClientId> find(List<String> relationshipManagerIds) {
        return delegate.find(relationshipManagerIds);
    }

    @CacheEvict(value = CLIENT_IDS_BY_RELATIONSHIP_MANAGERS_CACHE_NAME, allEntries = true, beforeInvocation = true)
    @Scheduled(cron = "${portfolio.adapter.dcl-client-service.client-ids-by-relationship-managers.cache.eviction:0 0 0,12 * * *}")
    public void clearClientIdsByRelationshipManagersCache() {
        log.info(CLIENT_IDS_BY_RELATIONSHIP_MANAGERS_CACHE_NAME + " cache emptied");
    }
}
```

### 3. Test with @ContextConfiguration

- Use testFixtures builders for concrete domain values — avoid `List.of()` or other empty stubs
- Keep assertions minimal: focus on caching behaviour, not domain logic

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DclFindClientIdsByRelationshipManagersCacheableRepositoryTest.TestConfig.class)
class DclFindClientIdsByRelationshipManagersCacheableRepositoryTest {

    @Autowired
    private FindClientIdsByRelationshipManagersRepository cacheableRepository;

    @Autowired
    @Qualifier("mockDelegate")
    private FindClientIdsByRelationshipManagersRepository mockDelegate;

    @Autowired
    private DclFindClientIdsByRelationshipManagersCacheableRepository cacheableRepositoryForCacheClearing;

    @BeforeEach
    void setUp() {
        Mockito.reset(mockDelegate);
        cacheableRepositoryForCacheClearing.clearClientIdsByRelationshipManagersCache();
    }

    //Actual tests and assertions
    
    @Configuration
    @EnableCaching(proxyTargetClass = true)
    static class TestConfig {

        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(CLIENT_IDS_BY_RELATIONSHIP_MANAGERS_CACHE_NAME);
        }

        @Bean
        @Qualifier("mockDelegate")
        public FindClientIdsByRelationshipManagersRepository mockDelegate() {
            return mock(FindClientIdsByRelationshipManagersRepository.class);
        }

        @Bean
        public DclFindClientIdsByRelationshipManagersCacheableRepository cacheableRepository(
                @Qualifier("mockDelegate") FindClientIdsByRelationshipManagersRepository delegate) {
            return new DclFindClientIdsByRelationshipManagersCacheableRepository(delegate);
        }
    }
}
```

## Key Points

- **@Primary**: Sets default bean; Spring injects the non-primary delegate automatically
- **@Cacheable**: Enables automatic caching
- **@ContextConfiguration**: Loads minimal test context
- **ConcurrentMapCacheManager**: Simple in-memory test cache
- **testFixtures builders**: Use real domain values, not stubs
- **Assertions**: Focus only on caching behavior
- **Cache clearing**: Autowire decorator and evict in @BeforeEach
