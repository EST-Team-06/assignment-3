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

- The only external dependency is the `api` variable of type `SocialMediaAPI` and its `post` method is used in `SocialMediaPoster`. It should be testing using doubles to avoid really depend on the real api

2. For the dependencies that should be tested using doubles, should the production code be refactored to make it possible? If so, do the refactoring and implement the tests.

- `api` is passed as parameter in the constructor of the class `SocialMediaPoster` which makes dependency injection possible. So we don't need to refactor the production code

3. What are the disadvantages of using doubles in your tests? Answer with examples from the SocialMediaPoster class.

- The main disadvantage is the tests become less realistic. In case we mock the api behavior not accurately, tests can artifically pass
- Another similar disadvantage is if the real implementation interface changes (e.g. its parameters or its method name), our mocks will not capture that automatically and require a manual adjustment. Our tests will again artifically pass


## Implementing `postBatch` with TDD

*1. Iteration: Happy Test*
- I first started with the test case with a batch that only contains one platform:
    - `postBatch(["Twitter"], "Hello World") == 1`
- I only extracted the first (only) element from the platforms list, and called `postContent`
- I then returned `1` or `0` depending on the success of `postContent`

    ```java
    @Test
    public int postBatch(List<String> platforms, String content) {
        String platform = platforms.get(0);

        return postContent(platform, content) ? 1 : 0;
    }
    ```

*2. Iteration: Handling Invalid Platforms*
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
    @Test
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