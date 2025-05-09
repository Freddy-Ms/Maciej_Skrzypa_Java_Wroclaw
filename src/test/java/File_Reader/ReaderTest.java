package File_Reader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Data_Classes.*;

import static org.junit.jupiter.api.Assertions.*;

class ReaderTest {

    String valid_payment_path = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Payment\\paymentmethods.json";
    String valid_orders_path = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Orders\\orders.json";
    // TestCases for paymentsmethods.json
    @Test
    public void Payments_not_table() {
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Payment\\paymentmethods1.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader("",filePath);
        });

        assertEquals("JSON table expected", thrown.getMessage());
    }

    @Test
    public void Invalid_payment_file() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            new Reader("","");
        });

        assertEquals("Error reading paymentmethods file", thrown.getMessage());
    }

    @Test
    public void Too_many_elements() {
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Payment\\paymentmethods2.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader("",filePath);
        });
        assertEquals("Too many elements in payment table", thrown.getMessage());
    }

    @Test
    public void Missing_required_element() {
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Payment\\paymentmethods3.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader("",filePath);
        });
        assertEquals("Missing required fields", thrown.getMessage());
    }

    @Test
    public void Discount_not_float() {
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Payment\\paymentmethods4.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader("",filePath);
        });
        assertEquals("Invalid value", thrown.getMessage());
    }

    @Test
    public void Limit_not_float() {
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Payment\\paymentmethods5.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader("",filePath);
        });
        assertEquals("Invalid value", thrown.getMessage());
    }

    @Test
    public void Discount_is_negative_number() {
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Payment\\paymentmethods6.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader("",filePath);
        });
        assertEquals("Discount must be greater than 0", thrown.getMessage());
    }

    @Test
    public void Limit_is_negative_number() {
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Payment\\paymentmethods7.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader("",filePath);
        });
        assertEquals("Limit must be greater than 0", thrown.getMessage());
    }

    // TestCases for orders.json
    @Test
    public void Orders_not_table(){
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Orders\\orders1.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader(filePath,valid_payment_path);
        });

        assertEquals("JSON table expected", thrown.getMessage());
    }

    @Test
    public void Orders_file_not_found() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            new Reader("",valid_payment_path);
        });

        assertEquals("Error reading order file", thrown.getMessage());
    }

    @Test
    public void Orders_missing_required_element() {
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Orders\\orders2.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader(filePath,valid_payment_path);
        });
        assertEquals("Missing required fields", thrown.getMessage());
    }

    @Test
    public void Orders_too_many_elements() {
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Orders\\orders3.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader(filePath,valid_payment_path);
        });
        assertEquals("Too many elements in order table", thrown.getMessage());
    }

    @Test
    public void Value_not_float() {
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Orders\\orders4.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader(filePath,valid_payment_path);
        });
        assertEquals("Invalid value", thrown.getMessage());
    }

    @Test
    public void Non_existing_promotion(){
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Orders\\orders5.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader(filePath,valid_payment_path);
        });
        assertEquals("Payment not found", thrown.getMessage());
    }

    @Test
    public void Promotions_not_in_table()
    {
        String filePath = "E:\\Maciej_Skrzypa_Java_Wroclaw\\src\\test\\instances\\Orders\\orders6.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Reader(filePath,valid_payment_path);
        });
        assertEquals("Array expected", thrown.getMessage());
    }

    @Test
    public void Proper_Files() {
        Reader reader = new Reader(valid_orders_path,valid_payment_path);
        List<Order> orders = reader.getOrders();
        Map<String, Payment> payments = reader.getPayments();

        Points points = new Points("PUNKTY",15,100);
        Card mZysk = new Card("mZysk",10,180);
        Card BosBankrut = new Card("BosBankrut",5,200);
        Map<String, Payment> payments2 = Map.of(
                "PUNKTY",points,
                "mZysk", mZysk,
                "BosBankrut", BosBankrut
        );
        assertEquals(payments2.keySet(),payments.keySet());

        for(String key : payments2.keySet()){
            Payment expected = payments2.get(key);
            Payment actual = payments.get(key);

            assertNotNull(actual);
            assertEquals(expected.getId(),actual.getId());
            assertEquals(expected.getDiscount(),actual.getDiscount());
            assertEquals(expected.getLimit(),actual.getLimit());
        }

       List<Order> orders2 = List.of(
               new Order("ORDER1",100,List.of(mZysk)),
               new Order("ORDER2", 200, List.of(BosBankrut)),
               new Order("ORDER3", 150, List.of(mZysk,BosBankrut)),
               new Order("ORDER4", 50)
       );

        assertEquals(orders2.size(), orders.size());

        for (int i = 0; i < orders.size(); i++) {
            Order actual = orders.get(i);
            Order expected = orders2.get(i);

            assertEquals(expected.id(),actual.id());
            assertEquals(expected.value(),actual.value());
            assertEquals(expected.promotions().size(),actual.promotions().size());

            for(int j = 0 ; j < expected.promotions().size(); j++){
                assertEquals(expected.promotions().get(j).getId(),actual.promotions().get(j).getId());
            }

        }


    }
}