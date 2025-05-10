package Solver;

import DataBinders.OrderPaymentBinder;
import DataBinders.OrderWithPromotions;
import DataBinders.PaymentOrderBinder;
import DataBinders.PaymentWithOrders;
import Data_Classes.Order;
import Data_Classes.Payment;
import File_Reader.OrderReader;
import File_Reader.PaymentReader;
import org.example.Config;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentWithOrdersSolverTest {

    PaymentOrderBinder binder = new PaymentOrderBinder();
    PaymentWithOrdersSolver solver = new PaymentWithOrdersSolver();


    @Test
    void testTheOutput(){

        OrderReader orderReader = new OrderReader("src/test/instances/Orders/orders.json");
        PaymentReader paymentReader = new PaymentReader("src/test/instances/Payment/paymentmethods.json");

        List<Order> orders = orderReader.read();
        List<Payment> payments = paymentReader.read();

        List<PaymentWithOrders> boundedPayment = binder.bind(payments,orders);

        Map<String,Float> result = solver.solve(boundedPayment, orders);

        assertEquals(155f, result.get("mZysk"), 0.01f);
        assertEquals(200f, result.get("BosBankrut"), 0.01f);
        assertEquals(100f, result.get(Config.POINTS), 0.01f);
    }
}