package dataclass.baseitem;

public interface Payment {

    float calculateDiscount(float value);

    public String id();

    public float discount();

    public float limit();

    public void pay(float value);

}

