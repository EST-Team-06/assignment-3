import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.junit.jupiter.api.Test;

class SocialMediaPosterTest {

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
        SocialMediaPoster poster = new SocialMediaPoster(new SocialMediaAPI());
        assertThrows(IllegalArgumentException.class, () -> {
            poster.postContent(platform, content);
        });
    }

    @Test
    public void validInput_postsSuccessfully() {
        SocialMediaPoster poster = new SocialMediaPoster(new SocialMediaAPI());
        boolean result = poster.postContent("Twitter", "Hello world");

        assertTrue(result);
    }

    // TDD tests for postBatch
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