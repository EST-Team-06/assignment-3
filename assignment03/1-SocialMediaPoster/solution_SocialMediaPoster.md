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
