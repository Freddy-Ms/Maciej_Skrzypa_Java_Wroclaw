package Data_Classes;
import java.util.List;

public record Order(String id, float value, List<Payment> promotions) {
    public Order(String id, float value) {
        this(id,value,List.of());
    }
}
