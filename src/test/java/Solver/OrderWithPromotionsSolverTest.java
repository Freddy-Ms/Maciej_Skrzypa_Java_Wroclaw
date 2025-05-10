package Solver;

import DataBinders.OrderPaymentBinder;
import DataBinders.OrderWithPromotions;
import Data_Classes.Order;
import Data_Classes.Payment;
import File_Reader.OrderReader;
import File_Reader.PaymentReader;
import org.example.Config;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class OrderWithPromotionsSolverTest {
    OrderPaymentBinder binder = new OrderPaymentBinder();
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