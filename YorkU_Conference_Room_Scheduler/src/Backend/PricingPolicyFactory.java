package Backend;

/**
 * Factory for creating pricing strategies based on user type
 * Implements Strategy pattern - creates appropriate pricing strategy for each user type
 */
public class PricingPolicyFactory {
    
    /**
     * Creates a pricing policy based on the user's account type
     * Uses Strategy pattern: StudentPricingStrategy, FacultyPricingStrategy, 
     * StaffPricingStrategy, ExternalPartnerPricingStrategy
     * 
     * @param user The user for whom to create a pricing policy
     * @return The appropriate PricingPolicy strategy for the user type
     */
    public PricingPolicy createPolicy(User user) {
        if (user == null) {
            // Default to standard policy if user is null
            return new StandardPricingPolicy();
        }
        
        String accountType = user.getAccountType();
        
        // Use Strategy pattern to select appropriate pricing strategy
        if ("Student".equals(accountType)) {
            return new StudentPricingStrategy();
        } else if ("Faculty".equals(accountType)) {
            return new FacultyPricingStrategy();
        } else if ("Staff".equals(accountType)) {
            return new StaffPricingStrategy();
        } else if ("External Partner".equals(accountType)) {
            return new ExternalPartnerPricingStrategy();
        }
        
        // Fallback to standard policy if account type is unknown
        return new StandardPricingPolicy();
    }
    
    /**
     * Creates a pricing policy without a user (for backward compatibility)
     * @return Standard pricing policy
     */
    public PricingPolicy createPolicy() {
        // For backward compatibility, return standard pricing policy
        return new StandardPricingPolicy();
    }
    
    /**
     * Creates a pricing policy by policy type string (for backward compatibility)
     * @param policyType The type of policy to create
     * @return The requested pricing policy
     */
    public PricingPolicy createPolicy(String policyType) {
        if (policyType == null || policyType.equals("Standard")) {
            return new StandardPricingPolicy();
        }
        // Can add more policy types here (e.g., DiscountPolicy, PremiumPolicy)
        return new StandardPricingPolicy();
    }
}


