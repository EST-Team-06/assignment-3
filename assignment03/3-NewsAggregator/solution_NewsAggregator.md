# News Aggregator Tests


## Concrete Implementation of Interfaces

- Looking at the code, we have the `NewsAggregator` class that depends on the `NewsAPI` and `ContentCache` interfaces
- Even though it's not requrired, I implemented concrete implementations of these interfaces [RealNewsAPI](https://github.com/EST-Team-06/assignment-3/blob/8eda3ba64cc75a44a010d88118810de138a66122/assignment03/3-NewsAggregator/src/main/java/RealNewsAPI.java) and [RealContentCacher](https://github.com/EST-Team-06/assignment-3/blob/8eda3ba64cc75a44a010d88118810de138a66122/assignment03/3-NewsAggregator/src/main/java/RealContentCacher.java) for testing purposes, they will help me writing the stub behavior for the tests


## Adjusting `NewsAggregator`

- The code looks testable, dependencies are injected via the constructor and the main method `getLatestNews` returns the list of news articles 
- I decided to slighlty adjust the `getLatestNews` method to allign with the readme instructions. I added fallback mechanisms for the cases
    - When the `NewsAPI` is down
    - When the fetch from the `NewsAPI` fails
    - When the response from the `NewsAPI` is null


## Writing Tests

- I first began with writing stubs for the dependencies: `StubNewsAPI` and `StubContentCacher`
- In `StubNewsAPI` we can control it's behavior with:
    - `available`: To simulate API availability
    - `throwOnFetch`: To simulate exceptions during fetching
    - `articles`: To control the articles returned by the API in case of a successful fetch
- In `StubContentCacher` we can control its behavior with:
    - `cachedArticles`: To control the articles returned from the cache
- Using these stubs, I then wrote tests for the `getLatestNews` method covering various scenarios
- The test cases include combinations of API availability, cache hits/misses, and edge cases like null or empty category to ensure 100% coverage
- Refer to [NewsAggregatorTest](https://github.com/EST-Team-06/assignment-3/blob/f578331cc8e2e5e0b6b1e1c4b1dbd24bbbbb9303/assignment03/3-NewsAggregator/src/test/java/NewsAggregatorTest.java) for the complete test suite


## Use of AI

- I used Gemini 3.1 Pro to rename the tests in a consistent way, as my initial naming was a bit inconsistent