package Backend;

/**
 * Pricing strategy for Faculty users
 * Implements Strategy pattern - computes $30/hour rate for faculty
 */
public class FacultyPricingStrategy implements PricingPolicy {
    
    private static final double HOURLY_RATE = 30.0;
    
    @Override
    public double calculateRate(User user) {
        // Verify user is Faculty
        if (user != null && "Faculty".equals(user.getAccountType())) {
            return HOURLY_RATE;
        }
        // If user type doesn't match, return 0 or throw exception
        // For now, return the rate anyway if user is null (defensive)
        return HOURLY_RATE;
    }
}

