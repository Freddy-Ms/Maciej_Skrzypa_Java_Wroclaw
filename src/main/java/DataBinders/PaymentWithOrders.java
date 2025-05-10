package DataBinders;

import Data_Classes.Order;
import Data_Classes.Payment;
import java.util.List;

public class PaymentWithOrders {
    private Payment payment;
    private List<Order> orders;

    public PaymentWithOrders(Payment payment, List<Order> orders) {
        this.payment = payment;
        this.orders = orders;
    }

    public Payment getPayment() {
        return payment;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
