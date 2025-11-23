package Backend;

/**
 * Pricing strategy for Student users
 * Implements Strategy pattern - computes $20/hour rate for students
 */
public class StudentPricingStrategy implements PricingPolicy {
    
    private static final double HOURLY_RATE = 20.0;
    
    @Override
    public double calculateRate(User user) {
        // Verify user is a Student
        if (user != null && "Student".equals(user.getAccountType())) {
            return HOURLY_RATE;
        }
        // If user type doesn't match, return 0 or throw exception
        // For now, return the rate anyway if user is null (defensive)
        return HOURLY_RATE;
    }
}

