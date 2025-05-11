package dataclass.datarepresentation;

import dataclass.baseitem.Payment;

import java.util.List;

public class OrderWithPromotions {
    private final String id;
    private float value;
    private final List<Payment> promotions;

    public OrderWithPromotions(String id, float value, List<Payment> promotions) {
        this.id = id;
        this.value = value;
        this.promotions = promotions;
    }

    public String id() {
        return id;
    }

    public float value() {
        return value;
    }

    public List<Payment> promotions() {
        return promotions;
    }

    public void pay(float amount) {
        this.value -= amount;
    }

    public void discount() {
        this.value -= this.value * 0.1f;
    }

    public void bought() {
        this.value = 0;
    }

}
