import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SocialMediaPosterTest {

    private SocialMediaAPI api;
    private SocialMediaPoster poster;

    @BeforeEach
    void setUp() {
        api = mock(SocialMediaAPI.class);
        poster = new SocialMediaPoster(api);

        setApiLimit(42);
        setAllPostsSuccessful();
    }
    
    private static Stream<Arguments> provideInvalidInputs() {
        return Stream.of(
            Arguments.of(null,              "Some content"),
            Arguments.of("",                "Some content"),
            Arguments.of("Some platform",   null),
            Arguments.of("Some platform",   ""),
            Arguments.of("Some platform",   "x".repeat(281))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidInputs")
    public void invalidInputs_throwsIllegalArgumentException(String platform, String content) {
        assertThrows(IllegalArgumentException.class, () -> {
            poster.postContent(platform, content);
        });
    }

    @Test
    public void validInput_postsSuccessfully() {
        boolean result = poster.postContent("Twitter", "Hello world");

        assertTrue(result);
    }

    // TDD tests for postBatch
    @Test
    public void postBatch_emptyPlatforms_returnsZero() {
        int result = poster.postBatch(Collections.emptyList(), "Hello world");

        assertEquals(0, result);
    }

    @Test
    public void postBatch_singlePlatform_returnsOne() {
        int result = poster.postBatch(Arrays.asList("Twitter"), "Hello world");

        assertEquals(1, result);
    }

    @Test
    public void postBatch_multiplePlatforms_returnsCorrectCount() {
        int result = poster.postBatch(Arrays.asList("Twitter", null, "", "Instagram", "Facebook"), "Hello world");

        assertEquals(3, result);
    }

    @Test
    public void postBatch_invalidContent_returnsZero() {
        int result = poster.postBatch(Arrays.asList("Twitter", null, "", "Instagram", "Facebook"), "");

        assertEquals(0, result);
    }

    @Test
    public void postBatch_exceedsLimit_returnsLimit() {
        List<String> platforms = new ArrayList<>();
        platforms.addAll(Collections.nCopies(42, "repetitivePlatform"));
        platforms.add("Facebook");

        int result = poster.postBatch(platforms, "Hello world");

        assertEquals(42, result);
        verify(api, never()).post("Facebook", "Hello world");
    }

    @Test
    public void postBatch_exceedsLimitWithOneInvalidPlatform_returnsLimit() {
        List<String> platforms = new ArrayList<>();
        platforms.addAll(Collections.nCopies(20, "repetitivePlatform"));
        platforms.add(""); // Invalid platform
        platforms.addAll(Collections.nCopies(21, "repetitivePlatform"));
        platforms.add("Facebook");

        int result = poster.postBatch(platforms, "Hello world");

        assertEquals(42, result);
        verify(api, times(1)).post("Facebook", "Hello world");
    }

    @Test
    public void postBatch_exceedsLimitWithUnsuccessfulPosts_returnsZero() {
        when(api.post("repetitivePlatform", "Hello world")).thenReturn(false);

        List<String> platforms = new ArrayList<>();
        platforms.addAll(Collections.nCopies(42, "repetitivePlatform"));
        platforms.add("Facebook");

        int result = poster.postBatch(platforms, "Hello world");

        assertEquals(0, result);
        verify(api, never()).post("Facebook", "Hello world");
    }

    private void setApiLimit(int limit) {
        when(api.getRateLimitRemaining()).thenReturn(limit);
    }

    private void setAllPostsSuccessful() {
        when(api.post(anyString(), anyString())).thenReturn(true);
    }
}