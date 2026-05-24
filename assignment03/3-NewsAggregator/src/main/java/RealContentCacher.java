import java.util.List;

public class RealContentCacher implements ContentCache {

    @Override
    public List<NewsArticle> getCachedArticles(String category) {
        return List.of(
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
    }

    @Override
    public void cacheArticles(String category, List<NewsArticle> articles) {
        // Would save them in DB or keep them in memory
        return;
    }
}
