public class OrderUpdate {
    private final String orderId;
    private final OrderStatus status;
    private final String milestoneName;

    public OrderUpdate(String orderId, OrderStatus status, String milestoneName) {
        this.orderId = orderId;
        this.status = status;
        this.milestoneName = milestoneName;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public boolean isDelivered() {
        return status == OrderStatus.DELIVERED;
    }
}
