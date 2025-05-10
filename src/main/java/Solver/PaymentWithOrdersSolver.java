package Solver;

import DataBinders.PaymentWithOrders;
import Data_Classes.Order;
import Data_Classes.Payment;
import org.example.Config;
import java.util.stream.Collectors;
import java.util.*;

public class PaymentWithOrdersSolver implements Solver<PaymentWithOrders, Order> {

    @Override
    public Map<String, Float> solve(List<PaymentWithOrders> paymentsWithOrders, List<Order> orders) {
        Map<String, Float> result = new HashMap<>();
        Map<String, Order> orderById = orders.stream()
                .collect(Collectors.toMap(Order::id, o -> o));

        paymentsWithOrders.sort((p1, p2) -> Float.compare(p2.getPayment().getLimit(), p1.getPayment().getLimit()));

        for (PaymentWithOrders pwo : paymentsWithOrders) {
            pwo.getOrders().sort((o1, o2) -> Float.compare(o2.value(), o1.value()));

            for (Order order : pwo.getOrders()) {
                Order originalOrder = orderById.get(order.id());
                if (originalOrder == null || originalOrder.value() == 0) continue;

                float amountToPay = originalOrder.value() - pwo.getPayment().calculateDiscount(originalOrder.value());
                if (pwo.getPayment().getLimit() >= amountToPay) {
                    pwo.getPayment().pay(amountToPay);
                    originalOrder.bought();
                    result.put(pwo.getPayment().getId(), result.getOrDefault(pwo.getPayment().getId(), 0.0f) + amountToPay);
                    break;
                }
            }
        }

        for (Order order : orders) {
            if (order.value() == 0) continue;

            Payment pointsPayment = paymentsWithOrders.stream()
                    .map(PaymentWithOrders::getPayment)
                    .filter(p -> p.getId().equals(Config.POINTS))
                    .findFirst()
                    .orElse(null);

            if (pointsPayment != null && pointsPayment.getLimit() > 0) {
                float orderValue = order.value();
                float pointsAvailable = pointsPayment.getLimit();
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
                        result.put(pointsPayment.getId(), result.getOrDefault(pointsPayment.getId(), 0.0f) + tenPercentOfOrder);

                        payRemaining(order, result, paymentsWithOrders, true);
                    } else {
                        order.discount();
                        order.pay(pointsAvailable);
                        pointsPayment.pay(pointsAvailable);
                        result.put(pointsPayment.getId(), result.getOrDefault(pointsPayment.getId(), 0.0f) + pointsAvailable);

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
                                result.put(pointsPayment.getId(), result.getOrDefault(pointsPayment.getId(), 0.0f) + tenPercent);
                                pointsAvailable -= tenPercent;
                            }
                        }
                    }

                    if (pointsAvailable > 0 && order.value() > 0) {
                        float toPayWithPoints = Math.min(pointsAvailable, order.value());
                        order.pay(toPayWithPoints);
                        pointsPayment.pay(toPayWithPoints);
                        result.put(pointsPayment.getId(), result.getOrDefault(pointsPayment.getId(), 0.0f) + toPayWithPoints);

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
            Payment payment = pwo.getPayment();
            if (order.value() > 0 && payment.getLimit() > 0) {
                if (excludePoints && payment.getId().equals(Config.POINTS)) {
                    continue;
                }
                float amountToPay = Math.min(payment.getLimit(), order.value());
                payment.pay(amountToPay);
                order.pay(amountToPay);
                result.put(payment.getId(), result.getOrDefault(payment.getId(), 0.0f) + amountToPay);

                if (order.value() == 0) {
                    order.bought();
                }
            }
        }
    }
}
