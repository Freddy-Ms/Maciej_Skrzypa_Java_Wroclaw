package DataBinders;

import Data_Classes.Payment;
import Data_Classes.Order;
import File_Reader.OrderReader;
import File_Reader.PaymentReader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderPaymentBinderTest {
    OrderPaymentBinder binder = new OrderPaymentBinder();

    @Test
    void PaymentIdNotFound() {
        OrderReader orderReader = new OrderReader("src/test/instances/Orders/orders7.json");
        PaymentReader paymentReader = new PaymentReader("src/test/instances/Payment/paymentmethods.json");

        List<Order> orders = orderReader.read();
        List<Payment> payments = paymentReader.read();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,() -> {
            binder.bind(orders, payments);
        });

        assertTrue(thrown.getMessage().contains("Payment ID not found:"));
    }

    @Test
    void BindOrdersWithPromotionsAndAddPoints()
    {
        OrderReader orderReader = new OrderReader("src/test/instances/Orders/orders.json");
        PaymentReader paymentReader = new PaymentReader("src/test/instances/Payment/paymentmethods.json");

        List<Order> orders = orderReader.read();
        List<Payment> payments = paymentReader.read();

        List<OrderWithPromotions> boundedOrders = binder.bind(orders, payments);
        for(OrderWithPromotions order : boundedOrders) {
            List<Payment> orderPromotions = order.promotions();

            for(Payment promotion : orderPromotions) {
                assertTrue(payments.contains(promotion));
            }
        }
    }

}