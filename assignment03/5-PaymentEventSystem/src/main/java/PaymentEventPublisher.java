import java.util.ArrayList;
import java.util.List;

public class PaymentEventPublisher {
    private final List<PaymentListener> listeners = new ArrayList<>();

    public void subscribe(PaymentListener listener) {
        listeners.add(listener);
    }

    public int listenerCount() {
        return listeners.size();
    }

    public void publishPaymentSuccess(PaymentEvent event) {
        for (PaymentListener listener : listeners) {
            listener.onPaymentSuccess(event);
        }
    }
}
