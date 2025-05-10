package File_Reader;

import Data_Classes.Payment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentReaderTest {

    String valid_payment_path = "src/test/instances/Payment/paymentmethods.json";

    @Test
    public void PaymentsNotTable() {
        String filePath = "src/test/instances/Payment/paymentmethods1.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });

        assertEquals("JSON array expected for payments", thrown.getMessage());
    }

    @Test
    public void InvalidPaymentFile() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            new PaymentReader("invalid_path.json").read();
        });

        assertTrue(thrown.getMessage().contains("Error reading file"));
    }

    @Test
    public void TooManyElements() {
        String filePath = "src/test/instances/Payment/paymentmethods2.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Too many elements in payment object", thrown.getMessage());
    }

    @Test
    public void MissingRequiredElement() {
        String filePath = "src/test/instances/Payment/paymentmethods3.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Missing required fields in payment", thrown.getMessage());
    }

    @Test
    public void DiscountNotFloat() {
        String filePath = "src/test/instances/Payment/paymentmethods4.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Invalid value", thrown.getMessage());
    }

    @Test
    public void LimitNotFloat() {
        String filePath = "src/test/instances/Payment/paymentmethods5.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Invalid value", thrown.getMessage());
    }

    @Test
    public void DiscountIsNegativeNumber() {
        String filePath = "src/test/instances/Payment/paymentmethods6.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Discount and limit must be greater than 0", thrown.getMessage());
    }

    @Test
    public void LimitIsNegativeNumber() {
        String filePath = "src/test/instances/Payment/paymentmethods7.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Discount and limit must be greater than 0", thrown.getMessage());
    }

    @Test
    public void ProperPaymentsFile() {
        PaymentReader reader = new PaymentReader(valid_payment_path);
        List<Payment> payments = reader.read();

        assertEquals(3, payments.size());

        Payment punkty = payments.stream()
                .filter(p -> p.getId().equals("PUNKTY"))
                .findFirst()
                .orElse(null);
        assertNotNull(punkty);
        assertEquals(15, punkty.getDiscount());
        assertEquals(100, punkty.getLimit());

        Payment mZysk = payments.stream()
                .filter(p -> p.getId().equals("mZysk"))
                .findFirst()
                .orElse(null);
        assertNotNull(mZysk);
        assertEquals(10, mZysk.getDiscount());
        assertEquals(180, mZysk.getLimit());

        Payment bosBankrut = payments.stream()
                .filter(p -> p.getId().equals("BosBankrut"))
                .findFirst()
                .orElse(null);
        assertNotNull(bosBankrut);
        assertEquals(5, bosBankrut.getDiscount());
        assertEquals(200, bosBankrut.getLimit());
    }
}
