public class Payment {

    private PaymentProcessor processor;

    public Payment(PaymentProcessor processor) {
        this.processor = processor;
    }

    public void setProcessor(PaymentProcessor processor) {
        this.processor = processor;
    }

    public boolean processPayment(double amount) {
        if (processor == null) {
            throw new IllegalStateException("No payment processor set");
        }
        return processor.charge(amount);
    }

    public boolean refundPayment(double amount) {
        if (processor == null) {
            throw new IllegalStateException("No payment processor set");
        }
        return processor.refund(amount);
    }
}