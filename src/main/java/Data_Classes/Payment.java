package Data_Classes;

public interface Payment {
    float calculateDiscount(float value);

    public String id();

    public float discount();

    public float limit();

    public boolean canPay(float value);

    public void pay(float value);

}
