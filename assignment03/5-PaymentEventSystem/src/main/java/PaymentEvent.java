public class PaymentEvent {
    private final String paymentId;
    private final String customerEmail;
    private final double amount;

    public PaymentEvent(String paymentId, String customerEmail, double amount) {
        this.paymentId = paymentId;
        this.customerEmail = customerEmail;
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public double getAmount() {
        return amount;
    }
}
