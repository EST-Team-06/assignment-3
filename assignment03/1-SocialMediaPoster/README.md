# SocialMediaPoster

### A. Posting to Social Media Platforms

The `SocialMediaPoster` class is responsible for posting content to various social media platforms.
It uses an external dependency: the `SocialMediaAPI` interface to interact with different social media platforms.
The `postContent` method takes a platform name and content, validates the input, and posts it using the API.

Write *unit tests* for the `postContent` method of the `SocialMediaPoster` class.

Answer the following questions:
1. What are the external dependencies? Which of these dependencies should be tested using doubles and which should not? Explain your rationale.
2. For the dependencies that should be tested using doubles, should the production code be refactored to make it possible? If so, do the refactoring and implement the tests.
3. What are the disadvantages of using doubles in your tests? Answer with examples from the `SocialMediaPoster` class.

### B. Batch Posting Feature

Extend the `SocialMediaPoster` to add a new method called `postBatch(List<String> platforms, String content)`. This method should attempt to post the same content to multiple social media platforms and return the count of successful posts. Note that if the number of posts passed is greater than the API limit, the method should only post the first N posts that fit within the API limit.

You must implement and test the `postBatch` feature starting with Test-Driven Development (TDD). Document every step of your TDD iterative process in your `solution_[problem name].md` file, including:

- Each test you wrote
- Each small feature or code change you made to make the test pass
- Refactorings, if any

Afterwards, answer the questions from part A for this part as well.

Automate the test cases using the **JUnit5** plugin.

*Note 1*: Remember the techniques we learnt in the first chapters: test for empty cases, boundaries, etc. where applicable.

*Note 2*: Follow the **principles and best practices of good and maintainable test code**.