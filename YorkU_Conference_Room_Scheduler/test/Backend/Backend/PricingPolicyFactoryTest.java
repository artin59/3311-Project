package Backend;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PricingPolicyFactoryTest {
    private PricingPolicyFactory factory;
    private Student student;
    private Faculty faculty;
    private Staff staff;
    private ExternalPartner partner;
    
    @Before
    public void setUp() {
        factory = new PricingPolicyFactory();
        student = new Student("student@yorku.ca", "pass", "12345678");
        faculty = new Faculty("faculty@yorku.ca", "pass", "FAC001");
        staff = new Staff("staff@yorku.ca", "pass", "STAFF001");
        partner = new ExternalPartner("partner@external.com", "pass", "ORG001");
    }
    
    @Test
    public void testCreatePolicy_Student() {
        PricingPolicy policy = factory.createPolicy(student);
        assertNotNull("Policy should not be null", policy);
        assertTrue("Should return StudentPricingStrategy", policy instanceof StudentPricingStrategy);
        assertEquals("Student rate should be 20.0", 20.0, policy.calculateRate(student), 0.01);
    }
    
    @Test
    public void testCreatePolicy_Faculty() {
        PricingPolicy policy = factory.createPolicy(faculty);
        assertNotNull("Policy should not be null", policy);
        assertTrue("Should return FacultyPricingStrategy", policy instanceof FacultyPricingStrategy);
    }
    
    @Test
    public void testCreatePolicy_Staff() {
        PricingPolicy policy = factory.createPolicy(staff);
        assertNotNull("Policy should not be null", policy);
        assertTrue("Should return StaffPricingStrategy", policy instanceof StaffPricingStrategy);
    }
    
    @Test
    public void testCreatePolicy_ExternalPartner() {
        PricingPolicy policy = factory.createPolicy(partner);
        assertNotNull("Policy should not be null", policy);
        assertTrue("Should return ExternalPartnerPricingStrategy", policy instanceof ExternalPartnerPricingStrategy);
    }
    
    @Test
    public void testCreatePolicy_NullUser() {
        PricingPolicy policy = factory.createPolicy((User) null);
        assertNotNull("Policy should not be null", policy);
        assertTrue("Should return StandardPricingPolicy for null user", policy instanceof StandardPricingPolicy);
    }
    
    @Test
    public void testCreatePolicy_NoUser() {
        PricingPolicy policy = factory.createPolicy();
        assertNotNull("Policy should not be null", policy);
        assertTrue("Should return StandardPricingPolicy", policy instanceof StandardPricingPolicy);
    }
    
    @Test
    public void testCreatePolicy_String() {
        PricingPolicy policy = factory.createPolicy("Standard");
        assertNotNull("Policy should not be null", policy);
        assertTrue("Should return StandardPricingPolicy", policy instanceof StandardPricingPolicy);
    }
}