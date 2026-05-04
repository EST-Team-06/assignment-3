import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewsAggregatorTest {

    private static final String CATEGORY = "news";
    private List<NewsArticle> apiArticles;
    private List<NewsArticle> cachedArticles;

    @BeforeEach
    public void setUp() {
        apiArticles = buildArticles("api");
        cachedArticles = buildArticles("cache");
    }

    @Test
    public void cacheMiss_fetchesFromApi_andCaches() {
        StubNewsAPI stubNewsAPI = new StubNewsAPI(true, false, apiArticles);
        StubContentCacher stubContentCache = new StubContentCacher(null);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        List<NewsArticle> result = aggregator.getLatestNews(CATEGORY);

        assertEquals(result, apiArticles);
        assertEquals(apiArticles, stubContentCache.getLastCachedArticles());
        assertTrue(stubContentCache.getCacheCalled());
    }

    @Test
    public void cacheHit_returnsCachedArticles_noApiCall() {
        StubNewsAPI stubNewsAPI = new StubNewsAPI(true, true, apiArticles);
        StubContentCacher stubContentCache = new StubContentCacher(cachedArticles); // Cache available

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        List<NewsArticle> result = aggregator.getLatestNews(CATEGORY);

        assertEquals(result, cachedArticles);
        assertFalse(stubContentCache.getCacheCalled());
        assertFalse(stubNewsAPI.wasFetchCalled());
    }

    @Test
    public void cacheEmpty_fetchesFromApi_andCaches() {
        StubNewsAPI stubNewsAPI = new StubNewsAPI(true, false, apiArticles);
        StubContentCacher stubContentCache = new StubContentCacher(List.of()); // Empty cache

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        List<NewsArticle> result = aggregator.getLatestNews(CATEGORY);

        assertEquals(result, apiArticles);
        assertTrue(stubNewsAPI.wasFetchCalled());
        assertTrue(stubContentCache.getCacheCalled());
    }

    @Test
    public void apiUnavailable_cacheMiss_throwsException_beforeFetch() {
        StubNewsAPI stubNewsAPI = new StubNewsAPI(false, true, List.of()); // They state their API is down
        StubContentCacher stubContentCache = new StubContentCacher(null);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        assertThrows(RuntimeException.class, () -> {
            aggregator.getLatestNews(CATEGORY);
        });
        assertFalse(stubNewsAPI.wasFetchCalled());
    }

    @Test
    public void apiUnavailable_cacheHit_returnsCachedArticles() {
        StubNewsAPI stubNewsAPI = new StubNewsAPI(false, true, List.of()); // They state their API is down
        StubContentCacher stubContentCache = new StubContentCacher(cachedArticles);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        List<NewsArticle> result = aggregator.getLatestNews(CATEGORY);

        assertEquals(result, cachedArticles);
        assertFalse(stubNewsAPI.wasFetchCalled());
        assertFalse(stubContentCache.getCacheCalled());
    }

    @Test
    public void apiThrows_cacheMiss_throwsException() {
        StubNewsAPI stubNewsAPI = new StubNewsAPI(true, true, List.of()); // They don't state their API is down
        StubContentCacher stubContentCache = new StubContentCacher(null);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);

        assertThrows(RuntimeException.class, () -> {
            aggregator.getLatestNews(CATEGORY);
        });
        assertTrue(stubNewsAPI.wasFetchCalled());
    }

    @Test
    public void apiThrows_cacheHit_returnsCachedArticles() {
        StubNewsAPI stubNewsAPI = new StubNewsAPI(true, true, List.of()); // They don't state their API is down
        StubContentCacher stubContentCache = new StubContentCacher(cachedArticles);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        List<NewsArticle> result = aggregator.getLatestNews(CATEGORY);

        assertEquals(result, cachedArticles);
        assertFalse(stubNewsAPI.wasFetchCalled());
        assertFalse(stubContentCache.getCacheCalled());
    }

    @Test
    public void apiReturnsNull_throwsException() {
        StubNewsAPI stubNewsAPI = new StubNewsAPI(true, false, null);
        StubContentCacher stubContentCache = new StubContentCacher(null);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);

        assertThrows(RuntimeException.class, () -> {
            aggregator.getLatestNews(CATEGORY);
        });
        assertTrue(stubNewsAPI.wasFetchCalled());
    }

    @Test
    public void categoryNull_throwsIllegalArgumentException() {
        StubNewsAPI stubNewsAPI = new StubNewsAPI(true, true, List.of());
        StubContentCacher stubContentCache = new StubContentCacher(List.of());

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        assertThrows(IllegalArgumentException.class, () -> {
            aggregator.getLatestNews(null);
        });
        assertFalse(stubNewsAPI.wasFetchCalled());
    }

    @Test
    public void categoryBlank_throwsIllegalArgumentException() {
        StubNewsAPI stubNewsAPI = new StubNewsAPI(true, true, List.of());
        StubContentCacher stubContentCache = new StubContentCacher(List.of());

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        assertThrows(IllegalArgumentException.class, () -> {
            aggregator.getLatestNews("  ");
        });
        assertFalse(stubNewsAPI.wasFetchCalled());
    }

	private class StubNewsAPI implements NewsAPI {

        private boolean available;
        private boolean throwOnFetch;
        private List<NewsArticle> articles;
        private boolean fetchCalled;

        public StubNewsAPI(boolean available, boolean throwOnFetch, List<NewsArticle> articles) {
            this.available = available;
            this.throwOnFetch = throwOnFetch;
            this.articles = articles;
        }

        @Override
        public List<NewsArticle> fetchNews(String category, int limit) {
            fetchCalled = true;
            if (throwOnFetch) {
                throw new IllegalArgumentException("NewsAPI is not available");
            }
            if (!isAvailable()) {
                throw new IllegalArgumentException("NewsAPI is not available");
            }

            return articles;
        }

        @Override
        public boolean isAvailable() {
            return available;
        }

        public boolean wasFetchCalled() {
            return fetchCalled;
        }
    }

    private class StubContentCacher implements ContentCache {

        private List<NewsArticle> cachedArticles;
        private boolean cacheCalled = false;
        private List<NewsArticle> lastCachedArticles;

        public StubContentCacher(List<NewsArticle> cachedArticles) {
            this.cachedArticles = cachedArticles;
        }

        @Override
        public List<NewsArticle> getCachedArticles(String category) {
            return cachedArticles;
        }

        @Override
        public void cacheArticles(String category, List<NewsArticle> articles) {
            cacheCalled = true;
            lastCachedArticles = articles;
        }

        public boolean getCacheCalled() {
            return cacheCalled;
        }
        public List<NewsArticle> getLastCachedArticles() {
            return lastCachedArticles;
        }
    }

    private List<NewsArticle> buildArticles(String prefix) {
        return List.of(
                new NewsArticle(prefix + "t_1", prefix + "c_1",prefix + "s_1", CATEGORY, 1),
                new NewsArticle(prefix + "t_2", prefix + "c_2",prefix + "s_2", CATEGORY, 2)
        );
    }
}