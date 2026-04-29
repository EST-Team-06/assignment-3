import java.util.List;

public class NewsAggregator {
    private final NewsAPI newsAPI;
    private final ContentCache contentCache;

    /**
     * Constructor
     * @param newsAPI the news API connector
     * @param contentCache the cache to be used
     */
    public NewsAggregator(NewsAPI newsAPI, ContentCache contentCache) {
        this.newsAPI = newsAPI;
        this.contentCache = contentCache;
    }
    
    /**
     * Gets the latest news for a specific category
     * @param category the news category
     * @return list of news articles
     */
    public List<NewsArticle> getLatestNews(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        
        // Try to get from cache first
        List<NewsArticle> cachedArticles = contentCache.getCachedArticles(category);
        if (cachedArticles != null && !cachedArticles.isEmpty()) {
            return cachedArticles;
        }
        
        // If not in cache or cache is empty, fetch from API
        List<NewsArticle> freshArticles = newsAPI.fetchNews(category, 10);
        
        // Cache the results for future requests
        contentCache.cacheArticles(category, freshArticles);
        
        return freshArticles;
    }
}