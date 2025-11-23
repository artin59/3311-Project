package Backend;

/**
 * Pricing strategy for External Partner users
 * Implements Strategy pattern - computes $50/hour rate for external partners
 */
public class ExternalPartnerPricingStrategy implements PricingPolicy {
    
    private static final double HOURLY_RATE = 50.0;
    
    @Override
    public double calculateRate(User user) {
        // Verify user is External Partner
        if (user != null && "External Partner".equals(user.getAccountType())) {
            return HOURLY_RATE;
        }
        // If user type doesn't match, return 0 or throw exception
        // For now, return the rate anyway if user is null (defensive)
        return HOURLY_RATE;
    }
}

