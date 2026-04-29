# PaymentEventSystem

An e-commerce system must perform two actions after a payment succeeds.
First, it generates an invoice because the successfully completed payment must be recorded formally; this task is handled by the `InvoiceService` class.

Second, it sends payment confirmation notifications to customers; this is handled by the `EmailService` class.
Both classes implement the `PaymentListener` interface, which defines the `onPaymentSuccess` method. This method is called after a payment succeeds.
For this to happen, these classes subscribe to the `PaymentEventPublisher` class, which is responsible for invoking `onPaymentSuccess` on each listener.
Click [here](https://en.wikipedia.org/wiki/Observer_pattern) if you want to learn more about the *Observer* pattern (not mandatory for the assignment).

Write unit tests for the `publishPaymentSuccess` method of the `PaymentEventPublisher` class using test doubles instead of the real implementations of the `EmailService` and the `InvoiceService`, after refactoring the code accordingly if needed.
Specifically, implement tests that cover the following scenarios:

### A. Number of invocations
First, test if the `onPaymentSuccess` method is called as many times as expected.
To do this, create a spy of the `PaymentEventPublisher` class so that you can verify interactions with it during the test.

### B. Content of invocations—`ArgumentCaptor`
Simply counting the invocations may not be enough; additionally, test if the details of the payment event (such as payment ID, customer email, amount) are correctly passed to the services. Use `ArgumentCaptor` to capture and verify these details.

### C. Content of invocations—Increasing observability
Instead of using `ArgumentCaptor`, you could increase the observability of the `EmailService` class to achieve the same goal.
Implement the necessary changes to improve observability and write additional tests to verify that the contents of the payment event messages are correct.

### D. Advantages
What are the advantages of the techniques you used in B. and C.?

Automate the test cases using **JUnit 5** plugin.

*Note 1*: Remember the techniques we learnt in the first chapters: test for empty cases, boundaries, etc. where applicable.

*Note 2*: It is important to follow the **principles and best practices of good and maintainable test code**.