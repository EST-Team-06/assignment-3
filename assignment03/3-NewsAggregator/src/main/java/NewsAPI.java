import java.util.List;

public interface NewsAPI {
    /**
     * Fetches latest news articles from a specific category
     * @param category the news category
     * @param limit maximum number of articles to fetch
     * @return list of news articles
     */
    List<NewsArticle> fetchNews(String category, int limit);
    
    /**
     * Checks if the API is available
     * @return true if API is available
     */
    boolean isAvailable();
}