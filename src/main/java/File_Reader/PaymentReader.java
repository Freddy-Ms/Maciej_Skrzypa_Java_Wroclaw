package File_Reader;

import Data_Classes.Card;
import Data_Classes.Payment;
import Data_Classes.Points;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.Config;

import java.util.ArrayList;
import java.util.List;

public class PaymentReader extends ReaderBase<List<Payment>> {

    public PaymentReader(String filename)
    {
        super(filename);
    }

    @Override
    public List<Payment> read() {
        List<Payment> payments = new ArrayList<>();
        JsonNode rootNode = readJsonFile();

        if (!rootNode.isArray())
            throw new IllegalArgumentException("JSON array expected for payments");

        for (JsonNode node : rootNode) {
            Payment payment = parsePayment(node);
            payments.add(payment);
        }
        return payments;
    }

    private Payment parsePayment(JsonNode node) {
        if (node.size() > 3)
            throw new IllegalArgumentException("Too many elements in payment object");

        if (!node.has("id") || !node.has("discount") || !node.has("limit"))
            throw new IllegalArgumentException("Missing required fields in payment");

        String id = node.get("id").asText();
        float discount;
        float limit;
        try{
            discount = Float.parseFloat(node.get("discount").asText());
            limit = Float.parseFloat(node.get("limit").asText());
        } catch (NumberFormatException e){
            throw new IllegalArgumentException("Invalid value");
        }


        if (discount <= 0 || limit <= 0)
            throw new IllegalArgumentException("Discount and limit must be greater than 0");


        if (Config.POINTS.equals(id))
            return new Points(id, discount, limit);
        else
            return new Card(id, discount, limit);

    }
}
