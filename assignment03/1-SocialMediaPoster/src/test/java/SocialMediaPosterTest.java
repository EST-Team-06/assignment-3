import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    @Test
    public void postBatch_invalidContent_returnsZero() {
        SocialMediaPoster poster = new SocialMediaPoster(new SocialMediaAPI());
        int result = poster.postBatch(Arrays.asList("Twitter", null, "", "Instagram", "Facebook"), "");

        assertEquals(0, result);
    }

    @Test
    public void postBatch_exceedsLimit_returnsLimit() {
        SocialMediaPoster poster = spy(new SocialMediaPoster(new SocialMediaAPI()));

        List<String> platforms = new ArrayList<>();
        platforms.addAll(Collections.nCopies(42, "repetitivePlatform"));
        platforms.add("Facebook");

        int result = poster.postBatch(platforms, "Hello world");

        assertEquals(42, result);
        verify(poster, never()).postContent("Facebook", "Hello world");
    }

    @Test
    public void postBatch_exceedsLimitWithOneInvalidPlatform_returnsLimit() {
        SocialMediaPoster poster = spy(new SocialMediaPoster(new SocialMediaAPI()));

        List<String> platforms = new ArrayList<>();
        platforms.addAll(Collections.nCopies(20, "repetitivePlatform"));
        platforms.add(""); // Invalid platform
        platforms.addAll(Collections.nCopies(21, "repetitivePlatform"));
        platforms.add("Facebook");

        int result = poster.postBatch(platforms, "Hello world");

        assertEquals(42, result);
        verify(poster, times(1)).postContent("Facebook", "Hello world");
    }
}