package DataBinders;
import Data_Classes.Order;
import Data_Classes.Payment;
import org.example.Config;

import java.util.ArrayList;
import java.util.List;

public class OrderPaymentBinder {
    public List<OrderWithPromotions> bind(List<Order> orders, List<Payment> payments) {
        List<OrderWithPromotions> result = new ArrayList<>();
        for (Order order : orders) {
            List<Payment> boundedPromotions = new ArrayList<>();

            if(order.promotions() != null && !order.promotions().isEmpty()) {
                for (String paymentID : order.promotions()) {
                    Payment matchingPayment = findPaymentById(payments, paymentID);
                    boundedPromotions.add(matchingPayment);
                }
            }
            // You can always pay with POINTS
            Payment points = findPaymentById(payments, Config.POINTS);
            boundedPromotions.add(points);

            OrderWithPromotions boundOrder = new OrderWithPromotions(order.id(), order.value(), boundedPromotions);
            result.add(boundOrder);
        }
        return result;
    }

    private Payment findPaymentById(List<Payment> payments, String id) {
        return payments.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Payment ID not found: " + id));
    }
}
