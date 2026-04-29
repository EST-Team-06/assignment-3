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
}