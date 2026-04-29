public class OrderTracker {
    private final DeliveryService deliveryService;
    private final OrderDashboardService dashboardService;
    private final AlertService alertService;

    public OrderTracker(DeliveryService deliveryService,
                        OrderDashboardService dashboardService,
                        AlertService alertService) {
        this.deliveryService = deliveryService;
        this.dashboardService = dashboardService;
        this.alertService = alertService;
    }

    public void updateOrderStatus(String orderId) {
        OrderUpdate latestUpdate = deliveryService.getLatestUpdate(orderId);
        if (latestUpdate != null) {
            dashboardService.updateStatus(orderId, latestUpdate.getStatus());
            if (latestUpdate.isDelivered()) {
                alertService.sendCustomerAlert(orderId,
                        "Your order has been delivered: " + latestUpdate.getMilestoneName());
            }
        } else {
            handleTrackingFailure(orderId);
        }
    }

    private void handleTrackingFailure(String orderId) {
        alertService.sendCustomerAlert(orderId,
                "Order tracking is temporarily unavailable. Please check again later.");
    }
}
