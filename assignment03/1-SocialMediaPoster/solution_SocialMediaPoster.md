# Social Media Poster

## Unittests

- I first started by writing test cases for the `postContent` method of the `SocialMediaPoster` class
- My [test suite](src/test/java/SocialMediaPosterTest.java) included:
    - A happy test with valid inputs 
    - Invalid inputs checking if the corresponding exception was thrown
        - Null platform
        - Empty platform
        - Null content
        - Empty content
        - Content with length 281

## Questions

1. What are the external dependencies? Which of these dependencies should be tested using doubles and which should not? Explain your rationale.

- The only external dependency is the `api` variable of type `SocialMediaAPI` and its `post` method is used in `SocialMediaPoster`. It should be tested using doubles to avoid really depending on the real api

2. For the dependencies that should be tested using doubles, should the production code be refactored to make it possible? If so, do the refactoring and implement the tests.

- `api` is passed as parameter in the constructor of the class `SocialMediaPoster` which makes dependency injection possible. So we don't need to refactor the production code

3. What are the disadvantages of using doubles in your tests? Answer with examples from the SocialMediaPoster class.

- The main disadvantage is the tests become less realistic. In case we mock the api behavior not accurately, tests can artificially pass
- Another similar disadvantage is if the real implementation interface changes (e.g. its parameters or its method name), our mocks will not capture that automatically and require a manual adjustment. Our tests will again artificially pass


## Implementing `postBatch` with TDD

*1. Iteration: Empty List*
- I first started with a very basic case: empty list of platforms should return 0
- I only had to return 0 to make the following test pass:
    ```java
    @Test
    public void postBatch_emptyPlatforms_returnsZero() {
        int result = poster.postBatch(Collections.emptyList(), "Hello world");

        assertEquals(0, result);
    }
    ```

*2. Iteration: Happy Test*
- I first started with the test case with a batch that only contains one platform:
    - `postBatch(["Twitter"], "Hello World") == 1`
- I only extracted the first (only) element from the platforms list, and called `postContent`
- I then returned `1` or `0` depending on the success of `postContent`

    ```java
    public int postBatch(List<String> platforms, String content) {
        if (platforms.isEmpty()) {
            return 0;
        }
        String platform = platforms.get(0);

        return postContent(platform, content) ? 1 : 0;
    }
    ```

*3. Iteration: Handling Invalid Platforms*
- Even though `postContent` handles the posting the content to a individual platform, we need to keep count of successful posts
- I added the following test case:
    ```python
    postBatch(
        ["Twitter", null, "", "Instagram", "Facebook"], "Hello World"
    ) == 3
    ```
- I slightly had to refactor the test code as well, because `List.of()` raises an exception when having at least one `null`. I changed it to `Arrays.asList()`
- I also had to refactor the production code to introduce looping over the list, and added a counter for failure
- At the end I returned `listSize - failureCount`

    ```java
    public int postBatch(List<String> platforms, String content) {
        int failedPosts = 0;
        for (String platform : platforms) {
            try {
                postContent(platform, content);
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to post to " + platform + ": " + e.getMessage());
                failedPosts++;
            }
        }
        return platforms.size() - failedPosts;
    }
    ```

*4. Iteration: Handling Invalid Content*
- I also need to handle the invalid content. In the case of invalid content, regardless of if the platforms are valid or not, all posts in the batch can't be posted so the method should return 0
- I added the following test case:
    ```python
    postBatch(
        ["Twitter", "null", "", "Instagram", "Facebook"], ""
    ) == 0
    ```
- I just handle this case by adding a condition at the beginning. If the content is invalid I early return 0 

    ```java
    public int postBatch(List<String> platforms, String content) {
        if (content == null || content.trim().isEmpty() || content.length() > 280) {
            return 0;
        }
        // The rest of the method
    }
    ```

*5. Iteration: Handling API Limit*
- So far, I handled the basic functionality but, didn't consider the API limit so I add the following tests:
    ```python
    postBatch(
        [...(42 platforms), "Facebook"], "Hello World"
    ) == 42
    ```
    ```java
    verify(api, never()).post("Facebook", "Hello World");
    ```
    ```python
    postBatch(
        [...(42 platforms with one of them being invalid), "Facebook"], "Hello World"
    ) == 42
    ```
    ```java
    verify(api, times(1)).post("Facebook", "Hello World");
    ```
- To make these tests pass, I do the following refactorings:
    - To get the successfully posted post count, I changed the logic from `listSize - failureCount` to `successCount`
    - And I make the method return the current count if it reached to the limit, and not post any more to the remaining platforms
    - I increased its count only if `api.post()` returned true. While it returns true at the moment by default it might also return false when it's down

*6. Iteration: Handling Unsuccessful API Calls*
- So far, I assumed API would always be available, so I also need to handle the cases where we wouldn't keep trying forever if the api calls don't succeed
    ```python
    postBatch(
        [...(42 unsuccessful calls), "Facebook"], "Hello World"
    ) == 0
    ```
- To achieve this, I introduced a new variable `apiCalls` for keeping track of the api calls
- This ensures that failed API calls still count against the API limit


## Questions for `postBatch`

- The answers stay mostly quite the same, as `postBatch` uses the same external dependency as `postContent`

1. What are the external dependencies? Which of these dependencies should be tested using doubles and which should not? Explain your rationale.

- The only external dependency is still the `api` variable of type `SocialMediaAPI` and its `post` method is used in `postContent()`. Additionally `getRateLimitRemaining()` is directly used in this function. It should be testing using doubles to avoid really depending on the real api

2. For the dependencies that should be tested using doubles, should the production code be refactored to make it possible? If so, do the refactoring and implement the tests.

- `api` is passed as parameter in the constructor of the class `SocialMediaPoster` which makes dependency injection possible. So again, we don't need to refactor the production code

3. What are the disadvantages of using doubles in your tests? Answer with examples from the SocialMediaPoster class.

- The main disadvantage is the tests become less realistic. In case we mock the api behavior not accurately, tests can artificially pass
- Another similar disadvantage is if the real implementation interface changes (e.g. its parameters or its method name), our mocks will not capture that automatically and require a manual adjustment. Our tests will again artificially pass