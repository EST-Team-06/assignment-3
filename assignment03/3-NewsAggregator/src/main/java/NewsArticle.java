import java.util.Objects;

public class NewsArticle {
    private final String title;
    private final String content;
    private final String source;
    private final String category;
    private final long timestamp;
    
    public NewsArticle(String title, String content, String source, String category, long timestamp) {
        this.title = title;
        this.content = content;
        this.source = source;
        this.category = category;
        this.timestamp = timestamp;
    }
    
    // Getters
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getSource() { return source; }
    public String getCategory() { return category; }
    public long getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return "NewsArticle{" +
                "title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", category='" + category + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsArticle)) return false;

        NewsArticle that = (NewsArticle) o;
        return timestamp == that.timestamp &&
                Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) &&
                Objects.equals(source, that.source) &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, source, category, timestamp);
    }
}