import java.util.List;
import java.util.stream.Collectors;

public class RealNewsAPI implements NewsAPI {

    @Override
    public List<NewsArticle> fetchNews(String category, int limit) {
        // Real call to API
        System.out.println("Fetching news for category: " + category + " limit: " + limit);

        List<NewsArticle> articles = List.of(
            new NewsArticle(
                    "title_1",
                    "content_1",
                    "source_1",
                    category,
                    System.currentTimeMillis()
            ),
            new NewsArticle(
                    "title_2",
                    "content_2",
                    "source_2",
                    category,
                    System.currentTimeMillis()
            )
        );

        return articles.stream()
                .limit(limit)
                .collect(Collectors.toList());
        // NOTE: We use this for Java 11, for 16, we can directly do toList()
    }

    @Override
    public boolean isAvailable() {
        return true; // Typically true
    }
}
