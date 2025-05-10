package filereader;

import dataclass.baseitem.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderReaderTest {

    String valid_orders_path = "src/test/instances/Orders/orders.json";

    @Test
    public void ordersNotArray(){
        String filePath = "src/test/instances/Orders/orders1.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new OrderReader(filePath).read();
        });

        assertEquals("JSON array expected for orders", thrown.getMessage());
    }

    @Test
    public void ordersFileNotFound() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            new OrderReader("invalid_path.json").read();
        });

        assertTrue(thrown.getMessage().contains("Error reading file"));
    }

    @Test
    public void ordersMissingRequiredElement() {
        String filePath = "src/test/instances/Orders/orders2.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new OrderReader(filePath).read();
        });
        assertEquals("Missing required fields in order", thrown.getMessage());
    }

    @Test
    public void ordersTooManyElements() {
        String filePath = "src/test/instances/Orders/orders3.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new OrderReader(filePath).read();
        });
        assertEquals("Too many elements in order object", thrown.getMessage());
    }

    @Test
    public void valueNotFloat() {
        String filePath = "src/test/instances/Orders/orders4.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new OrderReader(filePath).read();
        });
        assertEquals("Invalid value", thrown.getMessage());
    }

    @Test
    public void promotionsNotInArray()
    {
        String filePath = "src/test/instances/Orders/orders6.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new OrderReader(filePath).read();
        });
        assertEquals("Promotions field should be an array", thrown.getMessage());
    }

    @Test
    public void properOrdersFile() {
        OrderReader reader = new OrderReader(valid_orders_path);
        List<Order> orders = reader.read();

        assertEquals(4, orders.size());

        Order order1 = orders.get(0);
        assertEquals("ORDER1", order1.id());
        assertEquals(100, order1.value());
        assertEquals(List.of("mZysk"), order1.promotions());

        Order order2 = orders.get(1);
        assertEquals("ORDER2", order2.id());
        assertEquals(200, order2.value());
        assertEquals(List.of("BosBankrut"), order2.promotions());

        Order order3 = orders.get(2);
        assertEquals("ORDER3", order3.id());
        assertEquals(150, order3.value());
        assertEquals(List.of("mZysk", "BosBankrut"), order3.promotions());

        Order order4 = orders.get(3);
        assertEquals("ORDER4", order4.id());
        assertEquals(50, order4.value());
        assertNull(order4.promotions());
    }
}
