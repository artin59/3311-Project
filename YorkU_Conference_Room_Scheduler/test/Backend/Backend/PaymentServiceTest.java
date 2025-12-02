package Backend;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PaymentServiceTest {
    private PaymentService paymentService;
    private MockPaymentProcessor mockProcessor;
    
    static class MockPaymentProcessor implements PaymentProcessor {
        private double lastChargeAmount = 0;
        private double lastRefundAmount = 0;
        private boolean chargeShouldFail = false;
        private boolean refundShouldFail = false;
        
        @Override
        public boolean charge(double amount) {
            if (chargeShouldFail) return false;
            lastChargeAmount = amount;
            return true;
        }
        
        @Override
        public boolean refund(double amount) {
            if (refundShouldFail) return false;
            lastRefundAmount = amount;
            return true;
        }
        
        public double getLastChargeAmount() { return lastChargeAmount; }
        public double getLastRefundAmount() { return lastRefundAmount; }
        public void setChargeShouldFail(boolean fail) { chargeShouldFail = fail; }
        public void setRefundShouldFail(boolean fail) { refundShouldFail = fail; }
        public void reset() {
            lastChargeAmount = 0;
            lastRefundAmount = 0;
            chargeShouldFail = false;
            refundShouldFail = false;
        }
    }
    
    @Before
    public void setUp() {
        paymentService = PaymentService.getInstance();
        mockProcessor = new MockPaymentProcessor();
        paymentService.setProcessor(mockProcessor);
    }
    
    @Test
    public void testGetInstance() {
        PaymentService service1 = PaymentService.getInstance();
        PaymentService service2 = PaymentService.getInstance();
        assertSame("Should return same instance", service1, service2);
    }
    
    @Test
    public void testCharge_Success() {
        boolean result = paymentService.charge(100.0);
        assertTrue("Charge should succeed", result);
        assertEquals("Should charge correct amount", 100.0, mockProcessor.getLastChargeAmount(), 0.01);
    }
    
    @Test
    public void testCharge_NoProcessor() {
        paymentService.setProcessor(null);
        boolean result = paymentService.charge(100.0);
        assertFalse("Charge should fail without processor", result);
    }
    
    @Test
    public void testRefund_Success() {
        boolean result = paymentService.refund(50.0);
        assertTrue("Refund should succeed", result);
        assertEquals("Should refund correct amount", 50.0, mockProcessor.getLastRefundAmount(), 0.01);
    }
    
    @Test
    public void testRefund_NoProcessor() {
        paymentService.setProcessor(null);
        boolean result = paymentService.refund(50.0);
        assertFalse("Refund should fail without processor", result);
    }
    
    @Test
    public void testChargeAdditional_PositiveAmount() {
        boolean result = paymentService.chargeAdditional(75.0);
        assertTrue("ChargeAdditional should succeed", result);
        assertEquals("Should charge correct amount", 75.0, mockProcessor.getLastChargeAmount(), 0.01);
    }
    
    @Test
    public void testChargeAdditional_ZeroAmount() {
        boolean result = paymentService.chargeAdditional(0.0);
        assertTrue("ChargeAdditional should succeed for zero", result);
        assertEquals("Should not charge anything", 0.0, mockProcessor.getLastChargeAmount(), 0.01);
    }
    
    @Test
    public void testChargeAdditional_NegativeAmount() {
        boolean result = paymentService.chargeAdditional(-10.0);
        assertTrue("ChargeAdditional should succeed for negative (no charge)", result);
        assertEquals("Should not charge negative amount", 0.0, mockProcessor.getLastChargeAmount(), 0.01);
    }
    
    @Test
    public void testChargeAdditional_NoProcessor() {
        paymentService.setProcessor(null);
        boolean result = paymentService.chargeAdditional(100.0);
        assertTrue("ChargeAdditional should succeed even without processor", result);
    }
}