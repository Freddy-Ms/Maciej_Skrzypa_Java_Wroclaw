package dataclass.baseitem;

public class Points implements Payment
{

    private String id;
    private float discount;
    private float limit;

    public Points(String id, float discount, float limit)
    {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
    }
    @Override
    public String id() {
        return id;
    }

    @Override
    public float discount() {
        return discount;
    }

    @Override
    public float limit() {
        return limit;
    }



    @Override
    public float calculateDiscount(float value)
    {
        return this.discount * value / 100;
    }


    @Override
    public void pay(float value) {
        this.limit -= value;
    }
}
