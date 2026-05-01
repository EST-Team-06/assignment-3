import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

public class PaymentEventPublisherTest {

    @Test
    void checkPaymentSuccess() {
        PaymentEventPublisher paymentEventPublisher = new PaymentEventPublisher();
        PaymentListener listener = spy(PaymentListener.class);

        PaymentEvent event = new PaymentEvent("42", "tester@gmail.com", 42);
        paymentEventPublisher.subscribe(listener);
        paymentEventPublisher.publishPaymentSuccess(event);
        verify(listener).onPaymentSuccess(event);
    }

    @Test
    void checkPaymentContentV1() {
        PaymentEventPublisher paymentEventPublisher = new PaymentEventPublisher();
        PaymentListener listener = mock(PaymentListener.class);

        PaymentEvent event = new PaymentEvent("42", "tester@gmail.com", 42);

        paymentEventPublisher.subscribe(listener);
        paymentEventPublisher.publishPaymentSuccess(event);

        ArgumentCaptor<PaymentEvent> eventCaptor = ArgumentCaptor.forClass(PaymentEvent.class);
        verify(listener).onPaymentSuccess(eventCaptor.capture());
        PaymentEvent capturedEvent = eventCaptor.getValue();
        assertEquals(event, capturedEvent);
    }

    @Test
    void checkPaymentContentV2() {
        PaymentEventPublisher paymentEventPublisher = new PaymentEventPublisher();
        EmailService listener = new EmailService();
        PaymentEvent event = new PaymentEvent("42", "tester@gmail.com", 42);
        paymentEventPublisher.subscribe(listener);
        paymentEventPublisher.publishPaymentSuccess(event);
        assertEquals(event, listener.getLastSeenEvent());
    }
}
