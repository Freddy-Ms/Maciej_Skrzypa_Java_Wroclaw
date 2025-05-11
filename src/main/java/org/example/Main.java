package org.example;

import databinder.OrderToPayments;
import databinder.PaymentToOrders;
import dataclass.baseitem.Order;
import dataclass.baseitem.Payment;
import dataclass.datarepresentation.OrderWithPromotions;
import dataclass.datarepresentation.PaymentWithOrders;
import filereader.OrderReader;
import filereader.PaymentReader;
import solver.OrderToPaymentsSolver;
import solver.PaymentToOrdersSolver;
import solver.Solver;

import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        if (args.length != 2)
            System.out.println("Error: Provide 2 arguments: path to orders and payment methods ");

        String ordersPath = args[0];
        String paymentMethodsPath = args[1];


        OrderReader orderReader1 = new OrderReader(ordersPath);
        PaymentReader paymentReader1 = new PaymentReader(paymentMethodsPath);

        List<Order> orders1 = orderReader1.read();
        List<Payment> payments1 = paymentReader1.read();

        PaymentToOrders paymentBinder = new PaymentToOrders();
        List<PaymentWithOrders> paymentWithOrders = paymentBinder.bind(payments1, orders1);

        Solver<PaymentWithOrders, Order> paymentOrderSolver = new PaymentToOrdersSolver();
        Map<String, Float> resultPaymentOrderSolver = paymentOrderSolver.solve(paymentWithOrders, orders1);


        OrderReader orderReader2 = new OrderReader(ordersPath);
        PaymentReader paymentReader2 = new PaymentReader(paymentMethodsPath);

        List<Order> orders2 = orderReader2.read();
        List<Payment> payments2 = paymentReader2.read();

        OrderToPayments orderToPayments = new OrderToPayments();
        List<OrderWithPromotions> ordersWithPromotions = orderToPayments.bind(orders2, payments2);

        Solver<OrderWithPromotions, Payment> orderPaymentSolver = new OrderToPaymentsSolver();
        Map<String, Float> resultOrderPaymentSolver = orderPaymentSolver.solve(ordersWithPromotions, payments2);

        float sumPaymentOrderSolver = sum(resultPaymentOrderSolver);
        float sumOrderPromotionSolver = sum(resultOrderPaymentSolver);


        if (sumPaymentOrderSolver < sumOrderPromotionSolver) {
            printResult(resultPaymentOrderSolver);
        } else if (sumOrderPromotionSolver < sumPaymentOrderSolver) {
            printResult(resultOrderPaymentSolver);
        } else {
            float pointsUsedPaymentOrderSolver = resultPaymentOrderSolver.getOrDefault(Config.POINTS, 0.0f);
            float pointsUsedOrderPromotionSolver = resultOrderPaymentSolver.getOrDefault(Config.POINTS, 0.0f);
            if (pointsUsedPaymentOrderSolver < pointsUsedOrderPromotionSolver) {
                printResult(resultPaymentOrderSolver);
            } else if (pointsUsedOrderPromotionSolver < pointsUsedPaymentOrderSolver) {
                printResult(resultOrderPaymentSolver);
            } else {
                System.out.println("Draw for both methods! Possible solutions:");
                printResult(resultOrderPaymentSolver);
                System.out.println("-----------------");
                printResult(resultPaymentOrderSolver);
            }
        }


    }

    private static float sum(Map<String, Float> result) {
        float total = 0.0f;
        for (float value : result.values()) {
            total += value;
        }
        return total;
    }

    private static void printResult(Map<String, Float> result) {
        result.forEach((paymentID, amount) -> {
            System.out.println(paymentID + " " + amount);
        });
    }
}