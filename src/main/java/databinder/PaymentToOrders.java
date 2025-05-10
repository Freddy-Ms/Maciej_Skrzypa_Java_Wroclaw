package databinder;

import dataclass.baseitem.Order;
import dataclass.baseitem.Payment;
import dataclass.datarepresentation.PaymentWithOrders;
import org.example.Config;

import java.util.ArrayList;
import java.util.List;

public class PaymentToOrders {

    public List<PaymentWithOrders> bind(List<Payment> payments, List<Order> orders) {
        List<PaymentWithOrders> paymentWithOrders = new ArrayList<>();

        for(Payment payment : payments) {
            List<Order> matchedOrders = new ArrayList<>();

            for(Order order : orders) {
                if(Config.POINTS.equals(payment.id())) {
                    if(order.promotions() == null || !order.promotions().contains(Config.POINTS)) {
                        if(order.promotions() == null){
                            order.setPromotions(new ArrayList<>());
                        }
                        order.promotions().add(Config.POINTS);
                    }
                    matchedOrders.add(order);
                }
                else if (order.promotions() != null && order.promotions().contains(payment.id())) {
                    matchedOrders.add(order);
                }
            }
            paymentWithOrders.add(new PaymentWithOrders(payment, matchedOrders));
        }
        return paymentWithOrders;

    }

}
