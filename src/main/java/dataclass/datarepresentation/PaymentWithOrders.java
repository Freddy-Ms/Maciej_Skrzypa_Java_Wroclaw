package dataclass.datarepresentation;

import dataclass.baseitem.Order;
import dataclass.baseitem.Payment;

import java.util.List;

public class PaymentWithOrders {
    private final Payment payment;
    private final List<Order> orders;

    public PaymentWithOrders(Payment payment, List<Order> orders) {
        this.payment = payment;
        this.orders = orders;
    }

    public Payment payment() {
        return payment;
    }

    public List<Order> orders() {
        return orders;
    }
}
