package Backend;

/**
 * Pricing strategy for Staff users
 * Implements Strategy pattern - computes $40/hour rate for staff
 */
public class StaffPricingStrategy implements PricingPolicy {
    
    private static final double HOURLY_RATE = 40.0;
    
    @Override
    public double calculateRate(User user) {
        // Verify user is Staff
        if (user != null && "Staff".equals(user.getAccountType())) {
            return HOURLY_RATE;
        }
        // If user type doesn't match, return 0 or throw exception
        // For now, return the rate anyway if user is null (defensive)
        return HOURLY_RATE;
    }
}

