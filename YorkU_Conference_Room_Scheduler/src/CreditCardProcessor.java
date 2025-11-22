public class CreditCardProcessor implements PaymentProcessor {

    @Override
    public boolean charge(double amount) {
        System.out.println("Charging credit card: $" + amount);
        // fake success
        return true;
    }

    @Override
    public boolean refund(double amount) {
        System.out.println("Refunding credit card: $" + amount);
        // fake success
        return true;
    }
}