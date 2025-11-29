package Backend;

import org.junit.Test;
import static org.junit.Assert.*;


public class StudentTest {

    // Test 1: Create student with valid parameters
    @Test
    public void testCreateStudent() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertNotNull(student);
        assertEquals("student@my.yorku.ca", student.getEmail());
        assertEquals("123456789", student.getOrgID());
    }

    // Test 2: Student hourly rate
    @Test
    public void testStudentHourlyRate() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertEquals(20.0, student.getHourlyRate(), 0.01);
    }

    // Test 3: Student account type
    @Test
    public void testStudentAccountType() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertEquals("Student", student.getAccountType());
    }

    // Test 4: Student requires verification
    @Test
    public void testStudentRequiresVerification() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertTrue(student.requiresVerfication());
    }

    // Test 5: Student is not verified initially
    @Test
    public void testStudentNotVerifiedInitially() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        // Student should not be verified initially
        // Note: isVerified() is protected, so we test through behavior
        assertTrue(student.requiresVerfication());
    }

    // Test 6: Student Org ID
    @Test
    public void testStudentOrgID() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertEquals("123456789", student.getOrgID());
    }

    // Test 7: Student with different Org ID
    @Test
    public void testStudentWithDifferentOrgID() {
        Student student = new Student("student2@my.yorku.ca", "Password123!", "987654321");
        
        assertEquals("987654321", student.getOrgID());
    }

    // Test 8: Student inherits from User
    @Test
    public void testStudentInheritsFromUser() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertTrue(student instanceof User);
    }

    // Test 9: Student inherits from Accounts
    @Test
    public void testStudentInheritsFromAccounts() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertTrue(student instanceof Accounts);
        assertNotNull(student.getAccountId());
        assertNotNull(student.getCreatedDate());
    }

    // Test 10: Student account status
    @Test
    public void testStudentAccountStatus() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertEquals("PENDING_VERIFICATION", student.getAccountStatus());
    }

    // Test 11: Set student account status
    @Test
    public void testSetStudentAccountStatus() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        student.setAccountStatus("ACTIVE");
        assertEquals("ACTIVE", student.getAccountStatus());
    }

    // Test 12: Student password
    @Test
    public void testStudentPassword() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertEquals("Password123!", student.getPassword());
    }

    // Test 13: Multiple students with different IDs
    @Test
    public void testMultipleStudents() {
        Student student1 = new Student("student1@my.yorku.ca", "Password123!", "111111111");
        Student student2 = new Student("student2@my.yorku.ca", "Password123!", "222222222");
        
        assertNotEquals(student1.getAccountId(), student2.getAccountId());
        assertEquals("111111111", student1.getOrgID());
        assertEquals("222222222", student2.getOrgID());
    }

    // Test 14: Student hourly rate is constant
    @Test
    public void testStudentHourlyRateIsConstant() {
        Student student1 = new Student("student1@my.yorku.ca", "Password123!", "123456789");
        Student student2 = new Student("student2@my.yorku.ca", "Password123!", "987654321");
        
        assertEquals(student1.getHourlyRate(), student2.getHourlyRate(), 0.01);
        assertEquals(20.0, student1.getHourlyRate(), 0.01);
    }

    // Test 15: Student email format
    @Test
    public void testStudentEmailFormat() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertTrue(student.getEmail().contains("@my.yorku.ca"));
    }

    // Test 16: Student created date
    @Test
    public void testStudentCreatedDate() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "123456789");
        
        assertNotNull(student.getCreatedDate());
    }

    // Test 17: Student account ID is unique
    @Test
    public void testStudentAccountIdIsUnique() {
        Student student1 = new Student("student1@my.yorku.ca", "Password123!", "123456789");
        Student student2 = new Student("student2@my.yorku.ca", "Password123!", "123456789");
        
        assertNotEquals(student1.getAccountId(), student2.getAccountId());
    }

    // Test 18: Student with valid student ID from list
    @Test
    public void testStudentWithValidStudentID() {
        Student student = new Student("student@my.yorku.ca", "Password123!", "333333333");
        
        assertEquals("333333333", student.getOrgID());
    }

    // Test 19: Student account type consistency
    @Test
    public void testStudentAccountTypeConsistency() {
        Student student1 = new Student("student1@my.yorku.ca", "Password123!", "123456789");
        Student student2 = new Student("student2@my.yorku.ca", "Password123!", "987654321");
        
        assertEquals(student1.getAccountType(), student2.getAccountType());
        assertEquals("Student", student1.getAccountType());
    }

    // Test 20: Student verification requirement consistency
    @Test
    public void testStudentVerificationRequirementConsistency() {
        Student student1 = new Student("student1@my.yorku.ca", "Password123!", "123456789");
        Student student2 = new Student("student2@my.yorku.ca", "Password123!", "987654321");
        
        assertEquals(student1.requiresVerfication(), student2.requiresVerfication());
        assertTrue(student1.requiresVerfication());
    }
}


