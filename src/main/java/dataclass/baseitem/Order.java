package dataclass.baseitem;

import java.util.List;


public class Order {
    private String id;
    private float value;
    private List<String> promotions;

    public Order(String id, float value, List<String> promotions) {
        this.id = id;
        this.value = value;
        this.promotions = promotions;
    }
    public Order(String id, float value) {
        this.id = id;
        this.value = value;
    }
    public void setPromotions(List<String> promotions) {
        this.promotions = promotions;
    }

    public String id(){
        return id;
    }

    public float value(){
        return value;
    }

    public List<String> promotions(){
        return promotions;
    }

    public void buy(){
        this.value = 0;
    }

    public void pay(float value){
        this.value -= value;
    }

    public void discount(){
        this.value -= this.value * 0.1f;
    }
}

