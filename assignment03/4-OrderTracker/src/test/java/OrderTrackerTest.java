import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;


public class OrderTrackerTest {
    // TODO: Write your unit tests here.

    @Test
    public void checkDashboardUpdate() {
        DeliveryService deliveryService = mock(DeliveryService.class);
        OrderDashboardService orderDashboardService = mock(OrderDashboardService.class);
        AlertService alertService = mock(AlertService.class);
        OrderTracker orderTracker = new OrderTracker(
                deliveryService,
                orderDashboardService,
                alertService);

        OrderUpdate orderUpdate = new OrderUpdate("42", OrderStatus.ON_THE_WAY, "Testy");
        when(deliveryService.getLatestUpdate("42")).thenReturn(orderUpdate);
        orderTracker.updateOrderStatus("42");
        verify(orderDashboardService).updateStatus(orderUpdate.getOrderId(), orderUpdate.getStatus());
    }

    @Test
    public void checkAlertService() {
        DeliveryService deliveryService = mock(DeliveryService.class);
        OrderDashboardService orderDashboardService = mock(OrderDashboardService.class);
        AlertService alertService = mock(AlertService.class);
        OrderTracker orderTracker = new OrderTracker(
                deliveryService,
                orderDashboardService,
                alertService);
        OrderUpdate orderUpdate = new OrderUpdate("42", OrderStatus.DELIVERED, "Testy");
        when(deliveryService.getLatestUpdate("42")).thenReturn(orderUpdate);

        orderTracker.updateOrderStatus("42");

        ArgumentCaptor<String> orderIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        verify(alertService).sendCustomerAlert(
                orderIdCaptor.capture(),
                msgCaptor.capture()
        );

        String capturedOrderId = orderIdCaptor.getValue();
        String capturedMsg = msgCaptor.getValue();
        assertEquals(orderUpdate.getOrderId(), capturedOrderId);
        assertEquals(orderUpdate.getDeliveredMessage(), capturedMsg);
    }

    @Test
    public void checkTrackingFailure() {
        DeliveryService deliveryService = mock(DeliveryService.class);
        OrderDashboardService orderDashboardService = mock(OrderDashboardService.class);
        AlertService alertService = mock(AlertService.class);
        OrderTracker orderTracker = new OrderTracker(
                deliveryService,
                orderDashboardService,
                alertService);
        when(deliveryService.getLatestUpdate("42")).thenReturn(null);
        orderTracker.updateOrderStatus("42");

        ArgumentCaptor<String> orderIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        verify(alertService).sendCustomerAlert(
                orderIdCaptor.capture(),
                msgCaptor.capture()
        );

        String unavailableMsg = "Order tracking is temporarily unavailable. Please check again later.";
        String capturedOrderId = orderIdCaptor.getValue();
        String capturedMsg = msgCaptor.getValue();
        assertEquals("42", capturedOrderId);
        assertEquals(unavailableMsg, capturedMsg);
    }

}
