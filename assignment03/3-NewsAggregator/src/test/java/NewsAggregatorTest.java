import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewsAggregatorTest {

    @Test
    public void apiAvailable_returnArticles() {
        String category = "news";
        List<NewsArticle> articles = List.of(
                new NewsArticle("t_1","c_1","s_1", category, 1),
                new NewsArticle("t_2","c_2","s_2", category, 1)
        );

        NewsAPI stubNewsAPI = new StubNewsAPI(true, articles);

        // No cache
        ContentCache stubContentCache = new StubContentCacher(null);

        NewsAggregator aggregator = new NewsAggregator(stubNewsAPI, stubContentCache);
        List<NewsArticle> result = aggregator.getLatestNews(category);

        assertEquals(2, result.size());
        assertEquals(result, articles);

    }

	private class StubNewsAPI implements NewsAPI {

        private boolean available;
        private List<NewsArticle> articles;

        public StubNewsAPI(boolean available,  List<NewsArticle> articles) {
            this.available = available;
            this.articles = articles;
        }

        @Override
        public List<NewsArticle> fetchNews(String category, int limit) {
            if (!isAvailable()) {
                throw new IllegalStateException("NewsAPI is not available");
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

        public StubContentCacher(List<NewsArticle> cachedArticles) {
            this.cachedArticles = cachedArticles;
        }

        @Override
        public List<NewsArticle> getCachedArticles(String category) {
            return cachedArticles;
        }

        @Override
        public void cacheArticles(String category, List<NewsArticle> articles) {

        }
    }
}