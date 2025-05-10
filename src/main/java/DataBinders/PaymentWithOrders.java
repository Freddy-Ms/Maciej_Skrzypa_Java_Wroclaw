package DataBinders;

import Data_Classes.Order;
import Data_Classes.Payment;
import java.util.List;

public record PaymentWithOrders(Payment payment, List<Order> orders) {
}
