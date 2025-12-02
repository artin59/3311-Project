package Backend;

/**
 * Service for handling payments, refunds, and additional charges
 */
public class PaymentService {
    private static PaymentService instance;
    private PaymentProcessor processor;
    
    public PaymentService() {
        // Default processor - can be set via setProcessor
        this.processor = null;
    }
    
    public static PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentService();
        }
        return instance;
    }
    
    public void setProcessor(PaymentProcessor processor) {
        this.processor = processor;
    }
    
    /**
     * Process a payment/charge
     * @param amount The amount to charge
     * @return true if successful, false otherwise
     */
    public boolean charge(double amount) {
        if (processor == null) {
            System.err.println("PaymentService: No payment processor set");
            return false;
        }
        return processor.charge(amount);
    }
    
    /**
     * Process a refund
     * @param amount The amount to refund
     * @return true if successful, false otherwise
     */
    public boolean refund(double amount) {
        if (processor == null) {
            System.err.println("PaymentService: No payment processor set");
            return false;
        }
        return processor.refund(amount);
    }
    
    /**
     * Charge additional amount (for extensions or modifications)
     * @param amount The additional amount to charge
     * @return true if successful, false otherwise
     */
    public boolean chargeAdditional(double amount) {
        if (amount <= 0) {
            return true; // No charge needed
        }
        if (processor == null) {
            // For development/testing: allow extension to proceed even without payment processor
            System.out.println("PaymentService: No payment processor set, but allowing charge for development/testing");
            return true;
        }
        return processor.charge(amount);
    }
}

