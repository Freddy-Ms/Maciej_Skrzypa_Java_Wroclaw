package solver;

import dataclass.datarepresentation.OrderWithPromotions;

import java.util.*;

import dataclass.baseitem.Payment;
import org.example.Config;

public class OrderWithPromotionsSolver implements Solver<OrderWithPromotions, Payment> {

    @Override
    public Map<String,Float> solve(List<OrderWithPromotions> orders, List<Payment> paymentMethods){
        Map<String, Float> result = new HashMap<>();

        orders.sort((o1, o2) -> Float.compare(o2.value(), o1.value()));

        for(OrderWithPromotions order : orders){
            order.promotions().sort((p1, p2) -> Float.compare(p2.discount(), p1.discount()));

            for(Payment payment : order.promotions()) {
                float amountToPay = order.value() - payment.calculateDiscount(order.value());
                if(payment.limit() >= amountToPay) {
                    payment.pay(amountToPay);
                    order.bought();
                    result.put(payment.id(), result.getOrDefault(payment.id(), 0.0f) + amountToPay);
                    break;
                }
            }
        }

        for(OrderWithPromotions order : orders){
            if(order.value() == 0) continue;

            Payment pointsPayment = order.promotions().stream()
                    .filter(p -> p.id().equals(Config.POINTS))
                    .findFirst().orElse(null);

            if(pointsPayment != null && pointsPayment.limit() > 0) {
                float orderValue = order.value();
                float pointsAvailable = pointsPayment.limit();
                float tenPercentOfOrder = orderValue * 0.1f;

                if(pointsAvailable >= tenPercentOfOrder) {
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
                        result.put(
                                pointsPayment.id(),
                                result.getOrDefault(pointsPayment.id(), 0.0f) + tenPercentOfOrder
                        );

                        payRemaining(order,result,paymentMethods,true);
                    } else {
                        order.discount();
                        order.pay(pointsAvailable);
                        pointsPayment.pay(pointsAvailable);
                        result.put(
                                pointsPayment.id(),
                                result.getOrDefault(pointsPayment.id(), 0.0f) + pointsAvailable
                        );
                        if(order.value() > 0)
                            payRemaining(order,result,paymentMethods,false);
                    }
                } else{

                    for(OrderWithPromotions o : orders) {
                        if(o.value() > 0) {
                            float tenPercent = o.value() * 0.1f;
                            if(pointsAvailable >= tenPercent) {
                                order.discount();
                                o.pay(tenPercent);
                                pointsPayment.pay(tenPercent);

                                result.put(
                                        pointsPayment.id(),
                                        result.getOrDefault(pointsPayment.id(), 0.0f) + tenPercent
                                );
                                pointsAvailable -= tenPercent;
                            }
                        }
                    }
                    if(pointsAvailable > 0 && order.value() > 0) {
                        float toPayWithPoints = Math.min(pointsAvailable, order.value());
                        order.pay(toPayWithPoints);
                        pointsPayment.pay(toPayWithPoints);
                        result.put(
                                pointsPayment.id(),
                                result.getOrDefault(pointsPayment.id(), 0.0f) + toPayWithPoints
                        );
                        if(order.value() > 0)
                            payRemaining(order,result,paymentMethods,false);
                    }
                }
            }
        }

        return result;
    }

    private void payRemaining(OrderWithPromotions order, Map<String, Float> result, List<Payment> paymentMethods, boolean excludePoints) {
        for(Payment payment : paymentMethods) {
            if(order.value() > 0 && payment.limit() > 0) {
                if(excludePoints && payment.id().equals(Config.POINTS)) {continue;}
                float amountToPay = Math.min(payment.limit(), order.value());
                payment.pay(amountToPay);
                order.pay(amountToPay);
                result.put(payment.id(), result.getOrDefault(payment.id(), 0.0f) + amountToPay);
            }
        }
    }
}
