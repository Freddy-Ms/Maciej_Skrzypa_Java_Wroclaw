package Data_Classes;

import java.util.List;

public record OrderWithPayments(String id, float value, List<Payment> promotions) {
    public OrderWithPayments(String id, float value) {
        this(id,value,List.of());
    }
}
