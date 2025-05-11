package databinder;

import dataclass.baseitem.Order;
import dataclass.baseitem.Payment;
import dataclass.datarepresentation.PaymentWithOrders;
import filereader.OrderReader;
import filereader.PaymentReader;
import org.example.Config;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentToOrdersTest {

    @Test
    void bindAllOrdersToPointsPayment() {
        OrderReader orderReader = new OrderReader("src/test/instances/Orders/orders.json");
        PaymentReader paymentReader = new PaymentReader("src/test/instances/Payment/paymentmethods.json");

        List<Order> orders = orderReader.read();
        List<Payment> payments = paymentReader.read();

        PaymentToOrders binder = new PaymentToOrders();

        List<PaymentWithOrders> result = binder.bind(payments, orders);

        PaymentWithOrders pointsBiding = result.stream()
                .filter(pwo -> pwo.payment().id().equals(Config.POINTS))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Points payment not found"));

        assertEquals(orders.size(), pointsBiding.orders().size());

        pointsBiding.orders().forEach(order -> {
            assertTrue(order.promotions().contains(Config.POINTS));
        });

    }

    @Test
    void ordersCorrectlyAssignedToMatchingPayments() {
        OrderReader orderReader = new OrderReader("src/test/instances/Orders/orders.json");
        PaymentReader paymentReader = new PaymentReader("src/test/instances/Payment/paymentmethods.json");

        List<Order> orders = orderReader.read();
        List<Payment> payments = paymentReader.read();

        PaymentToOrders binder = new PaymentToOrders();
        List<PaymentWithOrders> result = binder.bind(payments, orders);

        for (PaymentWithOrders pwo : result) {
            for (Order order : pwo.orders()) {
                if (!pwo.payment().id().equals(Config.POINTS)) {
                    assertTrue(order.promotions().contains(pwo.payment().id()));
                }
            }
        }
    }


}