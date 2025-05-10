package DataBinders;

import Data_Classes.Order;
import Data_Classes.Payment;
import org.example.Config;

import java.util.ArrayList;
import java.util.List;

public class PaymentOrderBinder {

    public List<PaymentWithOrders> bind(List<Payment> payments, List<Order> orders) {
        List<PaymentWithOrders> paymentWithOrders = new ArrayList<>();

        for(Payment payment : payments) {
            List<Order> matchedOrders = new ArrayList<>();

            for(Order order : orders) {
                if(Config.POINTS.equals(payment.getId())) {
                    matchedOrders.add(addPoints(order));
                }
                else if (order.promotions().contains(payment.getId())) {
                    matchedOrders.add(order);
                }
            }
            paymentWithOrders.add(new PaymentWithOrders(payment, matchedOrders));
        }
        return paymentWithOrders;

    }

    private Order addPoints(Order order) {
        List<String> updatedPromotions = new ArrayList<>(order.promotions());
        updatedPromotions.add(Config.POINTS);
        return new Order(order.id(), order.value(), updatedPromotions);
    }
}
