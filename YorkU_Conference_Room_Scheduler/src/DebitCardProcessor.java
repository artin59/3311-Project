public class DebitCardProcessor implements PaymentProcessor {

    @Override
    public boolean charge(double amount) {
        System.out.println("Charging debit card: $" + amount);
        return true;
    }

    @Override
    public boolean refund(double amount) {
        System.out.println("Refunding debit card: $" + amount);
        return true;
    }
}