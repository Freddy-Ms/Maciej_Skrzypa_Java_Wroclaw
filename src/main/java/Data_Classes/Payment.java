package Data_Classes;

public interface Payment {
    float calculateDiscount(float value);

    public String getId();

    public float getDiscount();

    public float getLimit();
}
