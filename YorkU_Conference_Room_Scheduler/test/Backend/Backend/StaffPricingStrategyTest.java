package Backend;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class StaffPricingStrategyTest {
    private StaffPricingStrategy strategy;
    private Staff staff;
    
    @Before
    public void setUp() {
        strategy = new StaffPricingStrategy();
        staff = new Staff("staff@yorku.ca", "pass", "STAFF001");
    }
    
    @Test
    public void testCalculateRate_Staff() {
        double rate = strategy.calculateRate(staff);
        assertEquals("Staff rate should be 40.0", 40.0, rate, 0.01);
    }
    
    @Test
    public void testCalculateRate_NullUser() {
        double rate = strategy.calculateRate(null);
        assertEquals("Should return 40.0 even for null user", 40.0, rate, 0.01);
    }
}