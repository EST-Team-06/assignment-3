# NewsAggregator

The `NewsAggregator` class manages news aggregation for a content platform.
It integrates with an external `NewsAPI` to fetch the latest news articles and uses a `ContentCache` to optimize performance and reduce API calls.
The `getLatestNews` method retrieves news articles for a specific category, first checking the cache and falling back to the API if needed.

Write unit tests for the `getLatestNews` method in the `NewsAggregator` class, considering the following:

- Use of Doubles for NewsAPI and ContentCache. Identify external dependencies and implement tests using stubs to simulate API responses for different categories and content scenarios, as well as different caching behaviors.

- Simulate API downtimes and cache misses to test the system's resilience and fallback mechanisms. Verify that proper error handling and fallback logic are triggered when external services are unavailable.

- Test cache invalidation scenarios and ensure fresh data is fetched when appropriate.

Automate the test cases using the **JUnit5** plugin.

*Note 1*: Remember the techniques we learnt in the first chapters: test for empty cases, boundaries, etc. where applicable.

*Note 2*: Follow the **principles and best practices of good and maintainable test code**.