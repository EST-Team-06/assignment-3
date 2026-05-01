# Order Tracker Tests
## Accuracy of Status Updates
* To check if `updateOrderStatus` accurately updates the dashboard when new order data arrives, we have to concretely check if:
    * Order passed into `updateOrderStatus` is the same as sent to the Dashboard via `dashboardService.updateStatus`
    * Specifically, if the orderId and the status are the same; as the dashboard only needs those two.

* The code seems testable for this task. We have dependency injection, dependencies are passed into the `OrderTracker` constructor
  * Adheres to the Ports & Adapters concepts. All dependencies (ports) are indeed interfaces
  * Concrete implementations do not exist; which is good as we should not have to worry about them for testing.

* Since there is no actual dashboard, there is no state that I could check to see if a given order with orderId and status has been added to the dashboard.
  * I could make a Fake dashboard, i.e. implement a simple one that stores order id and status in an array and then check if they match with what I passed to `updateOrderStatus`
  * But after going over some Chapter 6 [code snippets](https://github.com/effective-software-testing/code/blob/main/ch6/src/test/java/ch6/bookstore/BookStoreTest.java), I decided to use Mockito.
* We do not have to mock `OrderUpdate` because that is an entity / simple class, we can use it directly.
* I cannot come up with any boundaries to check, as Java ensures the "input" is valid, there will not be a `OrderUpdate` without an order id or status; so `getOrderId` and `getStatus` cannot fail.
* I could try to make a parametrized test where I cover every `OrderUpdate` but I do not really see a benefit on testing this. A single valid `OrderUpdate` seems to be enough for me to check all kinda of order updates
  * `getOrderId` and `getStatus` are just getters and setters, we can assume they are correctly implemented and will not change based on what concrete order ids and status values are set.

## Notification of Key Events
* Implementing the test with Argument Captor was straight forward
* I decided to improve "maintainability" and "testability" by implementing a `getDelieveredMessage` method for the `OrderUpdate`
* That way, if anyone decides to change the message in the future, they only need to change it once and not at two different places
* It also gives slightly better control over these messages rather than having them has strings placed in the code.
* The main reason was of course that it made it easier to test, rather than me having to type out the exact same string, I can just call the method and check if the messages match.

## Response to Tracking Failures
* The implementation is almost identical to checking alerts, but we instead make delivery service return `null` rather than an `OrderUpdate`
* Then we just check if the right alert message is being set.