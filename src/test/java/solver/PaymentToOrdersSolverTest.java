package solver;

import databinder.PaymentToOrders;
import dataclass.baseitem.Order;
import dataclass.baseitem.Payment;
import dataclass.datarepresentation.PaymentWithOrders;
import filereader.OrderReader;
import filereader.PaymentReader;
import org.example.Config;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentToOrdersSolverTest {

    PaymentToOrders binder = new PaymentToOrders();
    PaymentToOrdersSolver solver = new PaymentToOrdersSolver();


    @Test
    void testTheOutput() {

        OrderReader orderReader = new OrderReader("src/test/instances/Orders/orders.json");
        PaymentReader paymentReader = new PaymentReader("src/test/instances/Payment/paymentmethods.json");

        List<Order> orders = orderReader.read();
        List<Payment> payments = paymentReader.read();

        List<PaymentWithOrders> boundedPayment = binder.bind(payments, orders);

        Map<String, Float> result = solver.solve(boundedPayment, orders);

        assertEquals(155f, result.get("mZysk"), 0.01f);
        assertEquals(200f, result.get("BosBankrut"), 0.01f);
        assertEquals(100f, result.get(Config.POINTS), 0.01f);
    }
}