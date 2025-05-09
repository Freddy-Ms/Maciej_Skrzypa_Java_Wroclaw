package File_Reader;
import Data_Classes.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Reader
{

    String filename_orders;
    String filename_payment;
    List<Order> orders = new ArrayList<>();
    Map<String, Payment> payments = new HashMap<>();

    public List<Order> getOrders() {
        return orders;
    }

    public Map<String, Payment> getPayments() {
        return payments;
    }

    public Reader(String filename_orders, String filename_payment)
    {
        this.filename_orders = filename_orders;
        this.filename_payment = filename_payment;
        read_payments();
        read_orders();
    }

    private void read_payments()
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Path path = Paths.get(filename_payment);
            byte[] jsonBytes = Files.readAllBytes(path);
            JsonNode rootNode = mapper.readTree(jsonBytes);

            if (rootNode.isArray())
            {
                for(JsonNode node : rootNode)
                    payment_add(node);
            } else
            {
                throw new IllegalArgumentException("JSON table expected");
            }
        } catch (IOException e)
            {
                throw new RuntimeException("Error reading paymentmethods file");
            }
    }

    private void read_orders()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            Path path = Paths.get(filename_orders);
            byte[] jsonBytes = Files.readAllBytes(path);
            JsonNode rootNode = mapper.readTree(jsonBytes);
            if (rootNode.isArray())
            {
                for(JsonNode node : rootNode)
                    order_add(node);
            } else
            {
                throw new IllegalArgumentException("JSON table expected");
            }
            System.out.println(orders);
        } catch (IOException e)
        {
            throw new RuntimeException("Error reading order file");
        }

    }

    private void payment_add(JsonNode node){

        if (node.size() > 3)
        {
            throw new IllegalArgumentException("Too many elements in payment table");
        }

        if (!node.has("id") || !node.has("discount") || !node.has("limit"))
        {
            throw new IllegalArgumentException("Missing required fields");
        }

        String id = node.get("id").asText();
        String discount_text = node.get("discount").asText();
        String limit_text = node.get("limit").asText();

        float discount;
        float limit;

        try
        {
            discount = Float.parseFloat(discount_text);
            limit = Float.parseFloat(limit_text);
        } catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid value");
        }


        if (discount <= 0)
        {
            throw new IllegalArgumentException("Discount must be greater than 0");
        }
        if (limit <= 0)
        {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }

        Payment payment;
        if ("PUNKTY".equals(id))
        {
            payment = new Points(id,discount,limit);
        } else
        {
            payment = new Card(id,discount,limit);
        }

        payments.put(id, payment);
        System.out.println("Added payment " + id + " " + discount + " " + limit);
    }

    private void order_add(JsonNode node){
        if(node.size() > 3)
        {
            throw new IllegalArgumentException("Too many elements in order table");
        }

        if(!node.has("id") || !node.has("value"))
        {
            throw new IllegalArgumentException("Missing required fields");
        }

        String id = node.get("id").asText();
        String value_text = node.get("value").asText();

        float value;

        try
        {
            value = Float.parseFloat(value_text);
        } catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid value");
        }

        if (node.has("promotions"))
        {
            JsonNode promotions_node = node.get("promotions");

            if(promotions_node.isArray()){
                List<Payment> promotions = new ArrayList<>();
                for(JsonNode promotion : promotions_node)
                {
                    Payment payment = payments.get(promotion.asText());

                    if(payment == null)
                        throw new IllegalArgumentException("Payment not found");

                    promotions.add(payment);
                    System.out.println("Added promotion " + promotion.asText() + " " + payment);
                }
                if (promotions.size() == 0)
                {
                    orders.add(new Order(id,value));
                }

                orders.add(new Order(id,value,promotions));
            } else
            {
                throw new IllegalArgumentException("Array expected");
            }
        } else
        {
            orders.add(new Order(id,value));
        }

    }




}
