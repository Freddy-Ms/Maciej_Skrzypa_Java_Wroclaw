package DataBinders;

import Data_Classes.Order;
import Data_Classes.Payment;
import File_Reader.OrderReader;
import File_Reader.PaymentReader;
import org.example.Config;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentOrderBinderTest {

    @Test
    void BindAllOrdersToPointsPayment() {
        OrderReader orderReader = new OrderReader("src/test/instances/Orders/orders.json");
        PaymentReader paymentReader = new PaymentReader("src/test/instances/Payment/paymentmethods.json");

        List<Order> orders = orderReader.read();
        List<Payment> payments = paymentReader.read();

        PaymentOrderBinder binder = new PaymentOrderBinder();

        List<PaymentWithOrders> result = binder.bind(payments,orders);

        PaymentWithOrders pointsBiding = result.stream()
                .filter(pwo -> pwo.getPayment().getId().equals(Config.POINTS))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Points payment not found"));

        assertEquals(orders.size(),pointsBiding.getOrders().size());

        pointsBiding.getOrders().forEach(order -> {
            assertTrue(order.promotions().contains(Config.POINTS));
        });

    }
    @Test
    void ordersCorrectlyAssignedToMatchingPayments() {
        OrderReader orderReader = new OrderReader("src/test/instances/Orders/orders.json");
        PaymentReader paymentReader = new PaymentReader("src/test/instances/Payment/paymentmethods.json");

        List<Order> orders = orderReader.read();
        List<Payment> payments = paymentReader.read();

        PaymentOrderBinder binder = new PaymentOrderBinder();
        List<PaymentWithOrders> result = binder.bind(payments, orders);

        for (PaymentWithOrders pwo : result) {
            for (Order order : pwo.getOrders()) {
                if (!pwo.getPayment().getId().equals(Config.POINTS)) {
                    assertTrue(order.promotions().contains(pwo.getPayment().getId()));
                }
            }
        }
    }


}