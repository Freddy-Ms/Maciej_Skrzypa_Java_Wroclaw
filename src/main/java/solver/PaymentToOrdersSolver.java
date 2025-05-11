package solver;

import dataclass.baseitem.Order;
import dataclass.baseitem.Payment;
import dataclass.datarepresentation.PaymentWithOrders;
import org.example.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentToOrdersSolver implements Solver<PaymentWithOrders, Order> {

    @Override
    public Map<String, Float> solve(List<PaymentWithOrders> paymentsWithOrders, List<Order> orders) {
        Map<String, Float> result = new HashMap<>();

        paymentsWithOrders.sort((p1, p2) -> Float.compare(p2.payment().limit(), p1.payment().limit()));

        for (PaymentWithOrders pwo : paymentsWithOrders) {
            pwo.orders().sort((o1, o2) -> Float.compare(o2.value(), o1.value()));

            for (Order order : pwo.orders()) {
                if (order == null || order.value() == 0) continue;

                float amountToPay = order.value() - pwo.payment().calculateDiscount(order.value());
                if (pwo.payment().limit() >= amountToPay) {
                    pwo.payment().pay(amountToPay);
                    order.buy();
                    result.put(pwo.payment().id(), result.getOrDefault(pwo.payment().id(), 0.0f) + amountToPay);
                    break;
                }
            }
        }

        orders.sort((o1, o2) -> Float.compare(o2.value(), o1.value()));

        for (Order order : orders) {
            if (order.value() == 0) continue;

            Payment pointsPayment = paymentsWithOrders.stream()
                    .map(PaymentWithOrders::payment)
                    .filter(p -> p.id().equals(Config.POINTS))
                    .findFirst()
                    .orElse(null);

            if (pointsPayment != null && pointsPayment.limit() > 0) {
                float orderValue = order.value();
                float pointsAvailable = pointsPayment.limit();
                float tenPercentOfOrder = orderValue * 0.1f;

                if (pointsAvailable >= tenPercentOfOrder) {
                    float remainingPointsAfter10Percent = pointsAvailable - tenPercentOfOrder;

                    boolean canCoverOtherOrders = orders.stream()
                            .filter(o -> o.value() > 0)
                            .anyMatch(o -> {
                                float order10Percent = o.value() * 0.1f;
                                return remainingPointsAfter10Percent >= order10Percent && order != o;
                            });

                    if (canCoverOtherOrders) {
                        order.discount();
                        order.pay(tenPercentOfOrder);
                        pointsPayment.pay(tenPercentOfOrder);
                        result.put(pointsPayment.id(), result.getOrDefault(pointsPayment.id(), 0.0f) + tenPercentOfOrder);

                        payRemaining(order, result, paymentsWithOrders, true);
                    } else {
                        order.discount();
                        order.pay(pointsAvailable);
                        pointsPayment.pay(pointsAvailable);
                        result.put(pointsPayment.id(), result.getOrDefault(pointsPayment.id(), 0.0f) + pointsAvailable);

                        if (order.value() > 0) {
                            payRemaining(order, result, paymentsWithOrders, false);
                        }
                    }
                } else {
                    for (Order o : orders) {
                        if (o.value() > 0) {
                            float tenPercent = o.value() * 0.1f;
                            if (pointsAvailable >= tenPercent) {
                                o.discount();
                                o.pay(tenPercent);
                                pointsPayment.pay(tenPercent);
                                result.put(pointsPayment.id(), result.getOrDefault(pointsPayment.id(), 0.0f) + tenPercent);
                                pointsAvailable -= tenPercent;
                            }
                        }
                    }

                    if (pointsAvailable > 0 && order.value() > 0) {
                        float toPayWithPoints = Math.min(pointsAvailable, order.value());
                        order.pay(toPayWithPoints);
                        pointsPayment.pay(toPayWithPoints);
                        result.put(pointsPayment.id(), result.getOrDefault(pointsPayment.id(), 0.0f) + toPayWithPoints);

                        if (order.value() > 0) {
                            payRemaining(order, result, paymentsWithOrders, false);
                        }
                    }
                }
            } else {
                payRemaining(order, result, paymentsWithOrders, false);
            }
        }

        return result;
    }

    private void payRemaining(Order order, Map<String, Float> result, List<PaymentWithOrders> paymentsWithOrders, boolean excludePoints) {
        for (PaymentWithOrders pwo : paymentsWithOrders) {
            Payment payment = pwo.payment();
            if (order.value() > 0 && payment.limit() > 0) {
                if (excludePoints && payment.id().equals(Config.POINTS)) {
                    continue;
                }
                float amountToPay = Math.min(payment.limit(), order.value());
                payment.pay(amountToPay);
                order.pay(amountToPay);
                result.put(payment.id(), result.getOrDefault(payment.id(), 0.0f) + amountToPay);

                if (order.value() == 0) {
                    order.buy();
                }
            }
        }
    }
}
