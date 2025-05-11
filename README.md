# Promocje dla metod płatności

## 1. Klasy reprezentujące podstawowe dane

W projekcie powstały dwie klasy, które na celu mają reprezentować dane, które został bezpośrednio odczytane z plików o formacie JSON.

```Java
public class Order {
    private final String id;
    private float value;
    private List<String> promotions;
}
```
```Java
public class Payment {
    private final String id;
    private final float discount;
    private float limit;
}
```
Opis:
- `Order` reprezentuje pojedyncze zamówienie, zawierjące identyfikator, wartość zamówienia oraz listę identyfikatorów promocji.
- `Payment` reprezentuję metodę płatności z przypisanym rabatem (`discount`) i dostępnym limitem (`limit`).

## 2. Odczytanie pliku JSON

Do odczytu plików JSON wykorzystano bibliotekę **Jackson**.

W tym celu:
- Utworzono klasę abstrakcyjną `FileReader`
- Klasa ta zawiera wspólną logikę do wczytywania danych JSON i mapowania ich na struktury Java.
- Z `FileReader` dziedziczą konkretne klasy obsługujące odpowiednio pliki `paymentmethods.json` oraz `order.json`.

## 3. Proces walidacji danych podczas odczytu
Podczas wczytywania danych z plików JSON zastosowano szereg weryfikacji, aby zapewnić poprawność i bezpieczeństwo danych.

**Wspólne walidacje**:
- **Struktura danych**:
  Sprawdzane jest, czy główny element JSON jest tablicą; w przeciwnym wypadku zwracany jest błąd
- **Liczba pól**:
  Jeśli liczba pól w pojedynczym elemencie tablicy przekracza 3, zgłaszany jest błąd.
- **Wymagane pola**:
  - Dla `Payment`: muszą występować pola `id`, `discount`, `limit`.
  - Dla `Order`: muszą występować pola `id`, `value`.
- **Poprawność wartości liczbowych**:
  Próba prasowania wartości `discount`, `limit`, `value` na `float`. Jeśli nie jest możliwa (np. niepoprawny format tekstowy), zgłaszany jest błąd.
- **Zakres wartości**:
  Sprawdzane jest, czy liczby nie są ujemne. Wartości mniejsze, bądź równe  zeru są nieakceptowane i skutkują błędem walidacji.

## 4. Klasy reprezentująca powiązane dane

W projekcie utworzono dwie klasy, które łącza ze sobą powiązane obiekty.

```Java
public class OrderWithPromotions {
    private final String id;
    private float value;
    private final List<Payment> promotions;
}
```
```Java
public class PaymentWithOrders {
    private final Payment payment;
    private final List<Order> orders;
}
```
Opis:
- `OrderWithPromotions`
  Klasa ta reprezentuje zamówienie (`Order`) wraz z dostępnymi dla niego promocjami (`List<Payment>`). W przeciwieństwie do klasy `Order`, gdzie lista promocji była reprezentowana jako lista identyfikatorów (`List<String>`), tutaj zawarte są bezpośrednio obiekty Payment, które można wykorzystać do wyliczeń i zastosowania rabatów.
- `PaymentWithOrders`
  Klasa wiąże konkretną metodę płatności (`Payment`) z listą zamówień (`List<Order>`), dla których ta metoda może zostać użyta.
  Pozwala to na łatwe grupowanie zamówień względem dostępnych metod płatności i upraszcza proces ich rozliczania.

## 5. Klasy, które łączą ze sobą powiązane dane
W projekcie powstały dwie klasy odpowiedzialne za mapowanie powiązań pomiędzy zamówieniami a metodami płatności: `OrderToPayments` oraz `PaymentToOrders`.
Każda z tych klas jako argumenty przyjmuje listy:
- zamówień (`List<Order>)
- metod płatności (`List<Payment>`),
które zostały uprzednio odczytane i przetworzone.

**Działanie klas**:
- **OrderToPayments**
  W tej klasie iterujemy po każdym zamówieniu z osobna. Dla każdego zamówienia sprawdzamy, czy ma przypisane promocje. Jeśli tak, przeszukujemy całą listę metod płatności. Gdy identyfikator pormocji (`promotion.id) odpowiada idetyfiaktorowi metody płatności (`payment.id'), wiążemy te dwa obiekty ze sobą.
- **PaymentToOrders**
  Tutaj działanie jest odwrotne. Iterujemy po każdej metodzie płatności. Dla każdej z nich sprawdzamy wszystkie zamówienia i, jeśli dane zamówienia posiada promocję odpowiadającą aktualnej metodzie płatnosći, tworzymy odpowiednie powiązanie.

**Obsługa punktów**

Ze względu na to, że płatność punktami jest możliwa dla każdego zamówienia, dodano spejalną logikę:
- Każde zamówienie otrzymuje możliwość opłacenia  punktami.
- Metoda płatnści odpowaidająca punktom zostaje powiązana z wszystkimi zamówieniami.

## 6. Klasy stosujące zachłanny algorytm optymalizacyjny

W projekcie zaimplementowano dwa algorytmy zachłanne rozwiązujące problem optymalnego przypisania płatności do zamówień. Powstał dwie osobne klasy solverów ze względu na różne strategie danych wejściowych:
- `PaymentToOrderSolver`, który działa na liście `PaymentWithOrders`
- `OrderToPaymentsSolver`, który pracuje na liście `OrderWithPromotions`

Poniżej opisano działanie `PaymentToOrdersSolver`

## Algorytm nr 1

**Argumenty wejściowe**:
- Lista powiązań płatności z zamówieniami (`List<PaymentWithOrders>`)
- Lista wszystkich zamówień (`List<Order>`)

**Główne kroki algorytmu**:
1. **Sortowanie płatności**:
   - Lista `PaymentWithOrders` sortowana jest malejąco według dostępnego limitu (`payment.limit()`).
2. **Pierwsza faza - próba bezpośredniego opłacenia zamówień**:
   - Iterujemy po każdej metodzie płatności od największego limitu do najmniejszego.
   - Dla każdej płatności iterujemy po przypisanych do niej zamówieniach, również posortowanych malejąco według wartości (`order.value()`).
   - Jeśli limit płatności pozwala na pokrycie wartości zamówienia pomniejszonej o rabat, opłacamy zamówienie w całości daną metodą i rejstrujemy to w wyniku.
   - Zamówienia, które moglibyśmy częściowo opłacić są pomijane w tej fazie
3. **Druga faza - użycie punktów**:
   - Dla zamówień nieopłacaonych sprawdzamy możliwość użycia płatności punktami.
   - Jeśli liczba dostępnych punktów pozwala na pokrycie przynajmniej 10% wartości zamówienia przyznjaemy rabat 10%, opłacamy 10% wartości punktami i symulujemy, czy reszta punktów może jeszcze zostać użyta dla innych zamówień.
     - Jeżeli tak - opłacamy tylko 10% aktualnego zamówienia punktami
     - Jeżeli nie płacamy możliwie jak najwięcej punktami
   - Jeśli aktualne zamówienie nie kwalifikuje się na rabat, próbujemy znaleźć inne zamówienie, dla którego 10% wartości może zostać pokryte puinktami i tam przypisujemy rabat.
4. **Ostateczne pokrycie pozostałej wartości zamówień:**
   - Dla pozostałej nieopłaconej części zamówień szukamy dowolnej dostępnej metody płatności (oprócz punktów, jesłi zostały już wykorzystane)
   - Płatności są przypisywanie proporcjonalnie do dostępnych limitów.
  
**Kluczowa funkcja pomocnicza**:
- `payRemaining`:
  - Funkcja próbująca opłacić zamówienie dowolnyim dostępnymi metodami płatności.
  - Można wyłączyć płatności punktami, aby priorytetowo korzystać z innych źródeł.

 ## Algorytm nr 2
Drugi algorytm zachłanny pracuje na innej strukturze danych, w której podstawą są zamówienia i przypisane do nich metody płatności.

**Dane wejściowe**:
- Lista powiązań zamówień z promocjami (`List<OrderWithPromotions>`)
- Lista wszystkich dostępnych metod płatności (`List<Payment>`)

**Główne kroki algorytmu**:
1. **Sortowanie zamówień**:
   - Zamówienia są sortowane malejąco według ich wartości
2. **Pierwsza faza -  przypisanie płatności do zamówień**:
   - Dla każdego zamówienia:
       - Sortujemy dostępne dla niego metody płatności malejąco według wartości zniżki
       - Następnie próbujemy opłacić zamówienie wybraną metodą:
           - Jeśli metoda płatności pozwala na pełne pokrycie zamówienia (po uwzględnieniu rabatu), opłacamy zamówienie tą metodą i rejstrujemy płatność.
           - Jeśli pełne pokrycie nie jest możliwe, przechodzimy do kolejnego zamówienia
   - Celem tej fazy jest maksymalne wykorzystanie najlepszych dostępnych rabatów.
3. **Druga faza - wykorzystanie punktów**:
   - Iterujemy ponownie po wszystkich zamówieniach.
   - Dla każdego zamówienia, które nie zostało jeszcze w pełni opłacone:
       - Sprawdzamy, czy mamy dostępną metodę płatności punktami.
       - Obliczamy 10% wartości zamówienia i sprawdzamy, czy dostępne punkty pozwalają na ich pokrycie
       - Jeśli tak:
           - Przyznajemy zamówieniu rabat 10%.
           - Opłacamy 10% wartości punktami.
           - Symulujemy, czy pozostałe punkty mogą zostać użyte do uzyskanioa rabatu w innych zamówieniach:
               - Jeśli tak - płacimy tylko 10% aktualnego zamówienia.
               - Jeśli nie - płacimy wszystkie dostępne punkty za aktualne zamówienie i ewnetualnie dopłacamy pozostałą kwotę innymi metodami. 
       - Jeśli punktów jest za mało:
           - Szukamy innego zamówienia, w którym dostepne punkty pozwolą pokryć 10% wartości i przyznać rabat
4. **Ostateczne pokrycie brakujących kwot**:
   - Jeśli po wykorzystaniu najlepszych metod i punktów pozstaje jeszcze jakaś część zamówienia do opłacenia:
       - Próbujemy opłacić pozostała kwotę dowolnymi dostępnymi metodami płatności
       - Możemy wyłączyć dalsze używanie punktów w tej fazie           

