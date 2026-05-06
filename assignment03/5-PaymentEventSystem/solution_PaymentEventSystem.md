# PaymentEventSystem Tests
## Number of invocations
* To check if `onPaymentSuccess` is called when `PaymentEventPublisher` publishes the event, the spy has to be `PaymentListener` and not `PaymentEventPublisher`
* We can then pass in as many spies as we want and then check if all of them are being called when an event is published.

## Content of invocations
* I implemented ArgumentCaptor on the `PaymentEvent` and capture it when `onPaymentSuccess` is called.

## Content of invocations - Increasing observability
* I refactored `EmailService` to return the last seen event to improve observability.
  * This was technically not necessary, as there was `getLastSentMessage` method but it made easier to implement the test.
* I then just check if the event I published is the same the `EmailService` listener has seen last.

## Advantages
* B: I did not have to touch the "production code" and was able to still check internal details via `ArgumentCaptor`
* C: I made the "production code" more observable; making it "easier" to write tests as I did not have to come up with a workaround using `ArgumentCaptor`. The tests became more readable as well