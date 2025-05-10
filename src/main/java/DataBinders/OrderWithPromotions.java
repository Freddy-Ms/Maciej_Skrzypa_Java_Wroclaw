package DataBinders;

import Data_Classes.Payment;
import java.util.List;

public record OrderWithPromotions(String id, float value, List<Payment> promotions)  {

}
