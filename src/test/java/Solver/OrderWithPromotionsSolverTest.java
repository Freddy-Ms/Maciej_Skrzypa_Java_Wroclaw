package solver;

import databinder.OrderToPayments;
import dataclass.datarepresentation.OrderWithPromotions;
import dataclass.baseitem.Order;
import dataclass.baseitem.Payment;
import filereader.OrderReader;
import filereader.PaymentReader;
import org.example.Config;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class OrderWithPromotionsSolverTest {
    OrderToPayments binder = new OrderToPayments();
    OrderWithPromotionsSolver solver = new OrderWithPromotionsSolver();


    @Test
    void testTheOutput(){
        OrderReader orderReader = new OrderReader("src/test/instances/Orders/orders.json");
        PaymentReader paymentReader = new PaymentReader("src/test/instances/Payment/paymentmethods.json");

        List<Order> orders = orderReader.read();
        List<Payment> payments = paymentReader.read();

        List<OrderWithPromotions> boundedOrders = binder.bind(orders, payments);

        Map<String,Float> result = solver.solve(boundedOrders, payments);

        assertEquals(165f, result.get("mZysk"), 0.01f);
        assertEquals(190f, result.get("BosBankrut"), 0.01f);
        assertEquals(100f, result.get(Config.POINTS), 0.01f);
    }
  
}