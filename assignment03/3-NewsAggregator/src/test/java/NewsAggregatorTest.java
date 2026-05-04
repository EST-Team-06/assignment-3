import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewsAggregatorTest {

    @Test
    public void apiAvailable_returnsArticles() {
        String category = "news";
        List<NewsArticle> articles = List.of(
                new NewsArticle("t_1","c_1","s_1", category, 1),
                new NewsArticle("t_2","c_2","s_2", category, 1)
        );

        NewsAPI stubNewsAPI = new StubNewsAPI(true, false, articles);

        // No cache
        ContentCache stubContentCache = new StubContentCacher(null);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        List<NewsArticle> result = aggregator.getLatestNews(category);

        assertEquals(2, result.size());
        assertEquals(result, articles);
    }

    @Test
    public void apiAvailable_cacheAvailable_returnsCachedArticles() {
        String category = "news";
        List<NewsArticle> articles = List.of(
                new NewsArticle("t_1","c_1","s_1", category, 1),
                new NewsArticle("t_2","c_2","s_2", category, 1)
        );
        // One more list of articles. If we use the one above, we might not know if it really used the cache
        List<NewsArticle> cachedArticles = List.of(
                new NewsArticle("cached_t_1","c_1","s_1", category, 1),
                new NewsArticle("cached_t_2","c_2","s_2", category, 1)
        );

        NewsAPI stubNewsAPI = new StubNewsAPI(true, false, articles);

        // Cache available
        ContentCache stubContentCache = new StubContentCacher(cachedArticles);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        List<NewsArticle> result = aggregator.getLatestNews(category);

        assertEquals(2, result.size());
        assertEquals(result, cachedArticles);
    }

    @Test
    public void apiNotAvailable_cacheEmpty_throwsException() {
        String category = "news";

        NewsAPI stubNewsAPI = new StubNewsAPI(false, false, List.of()); // They state their API is down
        ContentCache stubContentCache = new StubContentCacher(null);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        assertThrows(RuntimeException.class, () -> {
            aggregator.getLatestNews(category);
        });
    }

    @Test
    public void apiNotAvailableFromStatus_cacheAvailable_returnsCachedArticles() {
        String category = "news";
        List<NewsArticle> articles = List.of(
                new NewsArticle("t_1","c_1","s_1", category, 1),
                new NewsArticle("t_2","c_2","s_2", category, 1)
        );

        NewsAPI stubNewsAPI = new StubNewsAPI(false, false, List.of()); // They state their API is down
        ContentCache stubContentCache = new StubContentCacher(articles);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        List<NewsArticle> result = aggregator.getLatestNews(category);

        assertEquals(2, result.size());
        assertEquals(result, articles);
    }

    @Test
    public void apiNotAvailableFromStatus_cacheNotAvailable_throwsException() {
        String category = "news";
        List<NewsArticle> articles = List.of(
                new NewsArticle("t_1","c_1","s_1", category, 1),
                new NewsArticle("t_2","c_2","s_2", category, 1)
        );

        NewsAPI stubNewsAPI = new StubNewsAPI(true, true, List.of()); // They don't state their API is down
        ContentCache stubContentCache = new StubContentCacher(null);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);

        assertThrows(RuntimeException.class, () -> {
            aggregator.getLatestNews(category);
        });
    }

    @Test
    public void apiNotAvailable_cacheAvailable_throwsException() {
        String category = "news";
        List<NewsArticle> articles = List.of(
                new NewsArticle("t_1","c_1","s_1", category, 1),
                new NewsArticle("t_2","c_2","s_2", category, 1)
        );

        NewsAPI stubNewsAPI = new StubNewsAPI(true, true, List.of()); // They don't state their API is down
        ContentCache stubContentCache = new StubContentCacher(articles);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        List<NewsArticle> result = aggregator.getLatestNews(category);

        assertEquals(2, result.size());
        assertEquals(result, articles);
    }

    @Test
    public void apiAvailable_cacheNotAvailable_cachesArticles() {
        String category = "news";
        List<NewsArticle> articles = List.of(
                new NewsArticle("t_1","c_1","s_1", category, 1),
                new NewsArticle("t_2","c_2","s_2", category, 1)
        );

        NewsAPI stubNewsAPI = new StubNewsAPI(true, false, articles);
        StubContentCacher stubContentCache = new StubContentCacher(null);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        aggregator.getLatestNews(category);

        assertTrue(stubContentCache.getCacheCalled());
    }

    @Test
    public void apiAvailable_articlesNull_throwsException() {
        String category = "news";

        NewsAPI stubNewsAPI = new StubNewsAPI(true, true, null);
        ContentCache stubContentCache = new StubContentCacher(null);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);

        assertThrows(RuntimeException.class, () -> {
            aggregator.getLatestNews(category);
        });
    }

    @Test
    public void apiAvailable_categoryNull_throwsException() {
        String category = null;

        NewsAPI stubNewsAPI = new StubNewsAPI(true, true, List.of());
        ContentCache stubContentCache = new StubContentCacher(List.of());

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        assertThrows(IllegalArgumentException.class, () -> {
            aggregator.getLatestNews(category);
        });
    }

    @Test
    public void apiAvailable_categoryEmpty_throwsException() {
        String category = "  ";

        NewsAPI stubNewsAPI = new StubNewsAPI(true, true, List.of());
        ContentCache stubContentCache = new StubContentCacher(List.of());

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        assertThrows(IllegalArgumentException.class, () -> {
            aggregator.getLatestNews(category);
        });
    }

	private class StubNewsAPI implements NewsAPI {

        private boolean available;
        private boolean throwOnFetch;
        private List<NewsArticle> articles;

        public StubNewsAPI(boolean available, boolean throwOnFetch, List<NewsArticle> articles) {
            this.available = available;
            this.throwOnFetch = throwOnFetch;
            this.articles = articles;
        }

        @Override
        public List<NewsArticle> fetchNews(String category, int limit) {
            if (throwOnFetch) {
                throw new RuntimeException("NewsAPI is not available");
            }
            if (!isAvailable()) {
                throw new RuntimeException("NewsAPI is not available");
            }

            return articles;
        }

        @Override
        public boolean isAvailable() {
            return available;
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
}