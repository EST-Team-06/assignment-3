import java.util.List;

public interface ContentCache {
    /**
     * Gets cached news articles for a category
     * @param category the news category
     * @return list of cached articles, or null if not in cache
     */
    List<NewsArticle> getCachedArticles(String category);
    
    /**
     * Caches news articles for a category
     * @param category the news category
     * @param articles the articles to cache
     */
    void cacheArticles(String category, List<NewsArticle> articles);
}