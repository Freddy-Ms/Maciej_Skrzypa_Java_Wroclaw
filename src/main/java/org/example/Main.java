package org.example;

import databinder.OrderToPayments;
import dataclass.datarepresentation.OrderWithPromotions;
import databinder.PaymentToOrders;
import dataclass.datarepresentation.PaymentWithOrders;
import dataclass.baseitem.Payment;
import dataclass.baseitem.Order;
import filereader.OrderReader;
import filereader.PaymentReader;
import solver.Solver;
import solver.PaymentWithOrdersSolver;
import solver.OrderWithPromotionsSolver;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
            if(args.length != 2)
                System.out.println("Error: Provide 2 arguments: path to orders and paymentmethods ");

            String ordersPath = args[0];
            String paymentMethodsPath = args[1];

            OrderReader orderReader = new OrderReader(ordersPath);
            PaymentReader paymentReader = new PaymentReader(paymentMethodsPath);

            List<Order> orders = orderReader.read();
            List<Payment> payments = paymentReader.read();

            PaymentToOrders paymentBinder = new PaymentToOrders();
            OrderToPayments orderToPayments = new OrderToPayments();

            List<PaymentWithOrders> paymentWithOrders = paymentBinder.bind(payments,orders);
            List<OrderWithPromotions> ordersWithPromotions = orderToPayments.bind(orders,payments);

            Solver<PaymentWithOrders,Order> paymentOrderSolver = new PaymentWithOrdersSolver();
            Solver<OrderWithPromotions, Payment> orderPaymentSolver = new OrderWithPromotionsSolver();

            Map<String,Float> resultPaymentOrderSolver = paymentOrderSolver.solve(paymentWithOrders,orders);
            Map<String,Float> resultOrderPaymentSolver = orderPaymentSolver.solve(ordersWithPromotions,payments);

            float sumPaymentOrderSolver = sum(resultPaymentOrderSolver);
            float sumOrderPromotionSolver = sum(resultOrderPaymentSolver);

            if(sumPaymentOrderSolver < sumOrderPromotionSolver) {
                printResult(resultPaymentOrderSolver);
            } else if(sumOrderPromotionSolver < sumOrderPromotionSolver) {
                printResult(resultOrderPaymentSolver);
            } else {
                float pointsUsedPaymentOrderSolver = resultPaymentOrderSolver.getOrDefault(Config.POINTS,0.0f);
                float pointsUsedOrderPromotionSolver = resultOrderPaymentSolver.getOrDefault(Config.POINTS,0.0f);

                if(pointsUsedPaymentOrderSolver < pointsUsedOrderPromotionSolver) {
                    printResult(resultPaymentOrderSolver);
                } else if(pointsUsedOrderPromotionSolver < pointsUsedPaymentOrderSolver) {
                    printResult(resultOrderPaymentSolver);
                } else{
                    System.out.println("Draw for both methods! Possible solutions:");
                    printResult(resultOrderPaymentSolver);
                    printResult(resultPaymentOrderSolver);
                }
            }




    }
    private static float sum(Map<String,Float> result){
        float total = 0.0f;
        for(float value: result.values()){
            total += value;
        }
        return total;
    }
    private static void printResult(Map<String,Float> result){
        result.forEach((paymentID, amount) -> {
            System.out.println("Payment " + paymentID + ": " + amount);
        });
    }
}