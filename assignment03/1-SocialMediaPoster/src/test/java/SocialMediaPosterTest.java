import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SocialMediaPosterTest {

    @Test
    public void platformNull_throwsIllegalArgumentException() {
        SocialMediaPoster poster = new SocialMediaPoster(new SocialMediaAPI());
        assertThrows(IllegalArgumentException.class, () -> {
            poster.postContent(null, "Some content");
        });
    }

    @Test
    public void platformEmpty_throwsIllegalArgumentException() {
        SocialMediaPoster poster = new SocialMediaPoster(new SocialMediaAPI());
        assertThrows(IllegalArgumentException.class, () -> {
            poster.postContent("", "Some content");
        });
    }

    @Test
    public void contentNull_throwsIllegalArgumentException() {
        SocialMediaPoster poster = new SocialMediaPoster(new SocialMediaAPI());
        assertThrows(IllegalArgumentException.class, () -> {
            poster.postContent("Some platform", null);
        });
    }

    @Test
    public void contentEmpty_throwsIllegalArgumentException() {
        SocialMediaPoster poster = new SocialMediaPoster(new SocialMediaAPI());
        assertThrows(IllegalArgumentException.class, () -> {
            poster.postContent("Some platform", "");
        });
    }

    @Test
    public void contentTooLong_throwsIllegalArgumentException() {
        SocialMediaPoster poster = new SocialMediaPoster(new SocialMediaAPI());
        String longContent = "x".repeat(281); // Boundary testing with 281 characters
        assertThrows(IllegalArgumentException.class, () -> {
            poster.postContent("Some platform", longContent);
        });
    }

    @Test
    public void validInput_postsSuccessfully() {
        SocialMediaPoster poster = new SocialMediaPoster(new SocialMediaAPI());
        boolean result = poster.postContent("Twitter", "Hello, world!");
        assertTrue(result);
    }
}