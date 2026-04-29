public class EmailService implements PaymentListener {
    private String lastSentMessage;

    @Override
    public void onPaymentSuccess(PaymentEvent event) {
        lastSentMessage = "Payment confirmation sent to " + event.getCustomerEmail()
                + " for payment ID: " + event.getPaymentId()
                + " Amount: " + event.getAmount();
        System.out.println(lastSentMessage);
        // Assume here you would send a real email.
    }

    public String getLastSentMessage() {
        return lastSentMessage;
    }
}
