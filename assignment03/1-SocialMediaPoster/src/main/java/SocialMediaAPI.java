public class SocialMediaAPI {
    /**
     * Posts content to a social media platform
     * @param platform The platform name (e.g., "Twitter", "Facebook")
     * @param content The content to post
     * @return true if posting was successful, false otherwise
     */
    boolean post(String platform, String content) {
        // this print statement is simulating the posting of the data
        // in a production environment you would use some sort of library or external API
        System.out.println("Posting " + content + " to platform " + platform + ".");
        return true;
    }
    
    /**
     * Gets the current rate limit status
     * @return remaining posts allowed in current time window
     */
    int getRateLimitRemaining() {
        return 42; // the answer to everything
    }
}