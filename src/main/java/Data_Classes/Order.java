package Data_Classes;
import java.util.List;

public record Order(String id, float value, List<String> promotions) {
    public Order(String id, float value) {
        this(id,value,List.of());
    }

    public Order pay(float amount){
        return new Order(this.id,this.value - amount, this.promotions);
    }

    public boolean isPaid() {
        return value == 0;
    }
}

