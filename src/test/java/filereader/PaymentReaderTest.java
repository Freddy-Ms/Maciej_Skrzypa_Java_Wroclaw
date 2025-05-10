package filereader;

import dataclass.baseitem.Payment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentReaderTest {

    String valid_payment_path = "src/test/instances/Payment/paymentmethods.json";

    @Test
    public void paymentsNotTable() {
        String filePath = "src/test/instances/Payment/paymentmethods1.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });

        assertEquals("JSON array expected for payments", thrown.getMessage());
    }

    @Test
    public void invalidPaymentFile() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            new PaymentReader("invalid_path.json").read();
        });

        assertTrue(thrown.getMessage().contains("Error reading file"));
    }

    @Test
    public void tooManyElements() {
        String filePath = "src/test/instances/Payment/paymentmethods2.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Too many elements in payment object", thrown.getMessage());
    }

    @Test
    public void missingRequiredElement() {
        String filePath = "src/test/instances/Payment/paymentmethods3.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Missing required fields in payment", thrown.getMessage());
    }

    @Test
    public void discountNotFloat() {
        String filePath = "src/test/instances/Payment/paymentmethods4.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Invalid value", thrown.getMessage());
    }

    @Test
    public void limitNotFloat() {
        String filePath = "src/test/instances/Payment/paymentmethods5.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Invalid value", thrown.getMessage());
    }

    @Test
    public void discountIsNegativeNumber() {
        String filePath = "src/test/instances/Payment/paymentmethods6.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Discount and limit must be greater than 0", thrown.getMessage());
    }

    @Test
    public void limitIsNegativeNumber() {
        String filePath = "src/test/instances/Payment/paymentmethods7.json";
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new PaymentReader(filePath).read();
        });
        assertEquals("Discount and limit must be greater than 0", thrown.getMessage());
    }

    @Test
    public void properPaymentsFile() {
        PaymentReader reader = new PaymentReader(valid_payment_path);
        List<Payment> payments = reader.read();

        assertEquals(3, payments.size());

        Payment punkty = payments.stream()
                .filter(p -> p.id().equals("PUNKTY"))
                .findFirst()
                .orElse(null);
        assertNotNull(punkty);
        assertEquals(15, punkty.discount());
        assertEquals(100, punkty.limit());

        Payment mZysk = payments.stream()
                .filter(p -> p.id().equals("mZysk"))
                .findFirst()
                .orElse(null);
        assertNotNull(mZysk);
        assertEquals(10, mZysk.discount());
        assertEquals(180, mZysk.limit());

        Payment bosBankrut = payments.stream()
                .filter(p -> p.id().equals("BosBankrut"))
                .findFirst()
                .orElse(null);
        assertNotNull(bosBankrut);
        assertEquals(5, bosBankrut.discount());
        assertEquals(200, bosBankrut.limit());
    }
}
