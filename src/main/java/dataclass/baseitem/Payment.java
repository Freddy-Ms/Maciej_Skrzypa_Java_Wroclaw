package dataclass.baseitem;

public class Payment {

    private final String id;
    private final float discount;
    private float limit;

    public Payment(String id, float discount, float limit) {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
    }

    public String id() {
        return id;
    }


    public float discount() {
        return discount;
    }


    public float limit() {
        return limit;
    }


    public float calculateDiscount(float value) {
        return this.discount * value / 100;
    }


    public void pay(float value) {
        this.limit -= value;
    }

}
