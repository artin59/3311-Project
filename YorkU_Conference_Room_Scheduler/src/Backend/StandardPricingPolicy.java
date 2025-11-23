package Backend;

public class StandardPricingPolicy implements PricingPolicy {
    
    @Override
    public double calculateRate(User user) {
        // Use the existing getHourlyRate() method from User classes
        return user.getHourlyRate();
    }
}


