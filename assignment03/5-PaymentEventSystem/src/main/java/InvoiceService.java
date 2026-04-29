public class InvoiceService implements PaymentListener {

    @Override
    public void onPaymentSuccess(PaymentEvent event) {
        System.out.println("Generating invoice for payment ID: " + event.getPaymentId()
                + " Customer: " + event.getCustomerEmail()
                + " Amount: " + event.getAmount());
        // Imagine here you would generate and store an invoice.
    }
}
