package filereader;

import com.fasterxml.jackson.databind.JsonNode;
import dataclass.baseitem.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderReader extends FileReader<List<Order>> {

    public OrderReader(String filename) {
        super(filename);
    }

    @Override
    public List<Order> read() {
        List<Order> orders = new ArrayList<>();
        JsonNode rootNode = readJsonFile();

        if (!rootNode.isArray()) {
            throw new IllegalArgumentException("JSON array expected for orders");
        }

        for (JsonNode node : rootNode) {
            Order order = parseOrder(node);
            orders.add(order);
        }

        return orders;
    }

    private Order parseOrder(JsonNode node) {
        if (node.size() > 3) {
            throw new IllegalArgumentException("Too many elements in order object");
        }
        if (!node.has("id") || !node.has("value")) {
            throw new IllegalArgumentException("Missing required fields in order");
        }

        String id = node.get("id").asText();
        float value;
        try {
            value = Float.parseFloat(node.get("value").asText());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value");
        }

        if (node.has("promotions")) {
            JsonNode promotionsNode = node.get("promotions");

            if (!promotionsNode.isArray()) {
                throw new IllegalArgumentException("Promotions field should be an array");
            }

            List<String> promotions = new ArrayList<>();
            for (JsonNode promo : promotionsNode) {
                promotions.add(promo.asText());
            }

            return new Order(id, value, promotions);
        } else {
            return new Order(id, value);
        }
    }
}
