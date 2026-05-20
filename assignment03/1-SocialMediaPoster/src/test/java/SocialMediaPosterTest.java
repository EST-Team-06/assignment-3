import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

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
        boolean result = poster.postContent("Twitter", "Hello world");

        assertTrue(result);
    }

    @Test
    public void postBatch_singlePlatform_returnsOne() {
        SocialMediaPoster poster = new SocialMediaPoster(new SocialMediaAPI());
        int result = poster.postBatch(Arrays.asList("Twitter"), "Hello world");

        assertEquals(1, result);
    }

    @Test
    public void postBatch_multiplePlatforms_returnsCorrectCount() {
        SocialMediaPoster poster = new SocialMediaPoster(new SocialMediaAPI());
        int result = poster.postBatch(Arrays.asList("Twitter", null, "", "Instagram", "Facebook"), "Hello world");

        assertEquals(3, result);
    }
}