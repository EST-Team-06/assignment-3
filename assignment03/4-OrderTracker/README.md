# OrderTracker

The `OrderTracker` class manages the real-time status of food delivery orders. The system integrates with a `DeliveryService` to receive live status updates from the courier and an `OrderDashboardService` to update the status displayed to customers. Additionally, when an order reaches important milestones, the system triggers notifications using the `AlertService`.

Write *unit tests* for the `updateOrderStatus` method of the `OrderTracker` class using test doubles for the delivery, dashboard, and alert services.

Specifically, implement tests that cover the following scenarios:

### A. Accuracy of Status Updates
First, verify that the `updateOrderStatus` method accurately updates the dashboard when new order data is received.

### B. Notification of Key Events
Test if the system correctly triggers notifications when an order is delivered. Use `ArgumentCaptor` from `Mockito` to capture the details of arguments passed to the `AlertService` (Instead of just verifying that `AlertService` was called, verify what values it was called with).

### C. Response to Tracking Failures
Implement tests that simulate the temporary unavailability of delivery tracking data to see how the system handles failures or interruptions in updates.

Automate the test cases using **JUnit 5** plugin.

*Note 1*: Remember the techniques we learnt in the first chapters: test for empty cases, boundaries, etc. where applicable.

*Note 2*: Follow the **principles and best practices of good and maintainable test code**.