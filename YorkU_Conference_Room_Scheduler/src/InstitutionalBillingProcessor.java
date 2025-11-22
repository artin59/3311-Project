public class InstitutionalBillingProcessor implements PaymentProcessor {

    @Override
    public boolean charge(double amount) {
        System.out.println("Charging institutional account: $" + amount);
        return true;
    }

    @Override
    public boolean refund(double amount) {
        System.out.println("Refunding institutional account: $" + amount);
        return true;
    }
}