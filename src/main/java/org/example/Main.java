package org.example;

import DataBinders.OrderPaymentBinder;
import DataBinders.OrderWithPromotions;
import DataBinders.PaymentOrderBinder;
import DataBinders.PaymentWithOrders;
import Data_Classes.Payment;
import Data_Classes.Order;
import File_Reader.OrderReader;
import File_Reader.PaymentReader;
import Solver.Solver;
import Solver.PaymentWithOrdersSolver;
import Solver.OrderWithPromotionsSolver;
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

            PaymentOrderBinder paymentBinder = new PaymentOrderBinder();
            OrderPaymentBinder orderPaymentBinder = new OrderPaymentBinder();

            List<PaymentWithOrders> paymentWithOrders = paymentBinder.bind(payments,orders);
            List<OrderWithPromotions> ordersWithPromotions = orderPaymentBinder.bind(orders,payments);

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