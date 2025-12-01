package Backend;

import Backend.ValidationUtil;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValidationUtilTest {

    // ----------------------------------------------------------
    // EMAIL TESTS
    // ----------------------------------------------------------

    @Test
    public void testValidEmailGeneral5() {
        assertTrue(ValidationUtil.isValidEmail("test123@gmail.com"));
        assertTrue(ValidationUtil.isValidEmail("john.doe123@company.ca"));
    }

    @Test
    public void testInvalidEmailGeneral5() {
        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail("invalidemail"));
        assertFalse(ValidationUtil.isValidEmail("noatsign.com"));
        assertFalse(ValidationUtil.isValidEmail("bad@format"));
    }

    @Test
    public void testValidStudentEmail5() {
        assertTrue(ValidationUtil.isValidEmail("abc123@my.yorku.ca", "Student"));
    }

    @Test
    public void testInvalidStudentEmail5() {
        assertFalse(ValidationUtil.isValidEmail("student@gmail.com", "Student"));
        assertFalse(ValidationUtil.isValidEmail("xyz@my.yorku", "Student"));
        assertFalse(ValidationUtil.isValidEmail("wrong@yorku.ca", "Student"));
    }

    @Test
    public void testValidEmailNonStudent5() {
        assertTrue(ValidationUtil.isValidEmail("professor@yorku.ca", "Faculty"));
    }

    // ----------------------------------------------------------
    // PASSWORD TESTS
    // ----------------------------------------------------------

    @Test
    public void testValidPassword5() {
        assertTrue(ValidationUtil.isValidPassword("Abcdef1!"));
    }

    @Test
    public void testInvalidPasswordTooShort5() {
        assertFalse(ValidationUtil.isValidPassword("Ab1!"));
    }

    @Test
    public void testInvalidPasswordMissingUppercase5() {
        assertFalse(ValidationUtil.isValidPassword("abcdef1!"));
    }

    @Test
    public void testInvalidPasswordMissingLowercase5() {
        assertFalse(ValidationUtil.isValidPassword("ABCDEF1!"));
    }

    @Test
    public void testInvalidPasswordMissingDigit5() {
        assertFalse(ValidationUtil.isValidPassword("Abcdefg!"));
    }

    @Test
    public void testInvalidPasswordMissingSpecial5() {
        assertFalse(ValidationUtil.isValidPassword("Abcdefg1"));
    }

    // ----------------------------------------------------------
    // ORG ID TESTS
    // ----------------------------------------------------------

    @Test
    public void testValidOrgIdGeneral5() {
        assertTrue(ValidationUtil.isValidOrgId("123456789"));
    }

    @Test
    public void testInvalidOrgIdLength5() {
        assertFalse(ValidationUtil.isValidOrgId("12345"));
        assertFalse(ValidationUtil.isValidOrgId("1234567890"));
    }

    @Test
    public void testValidStudentOrgId5() {
        // One of the predefined IDs
        assertTrue(ValidationUtil.isValidOrgId("123456789", "Student"));
    }

    @Test
    public void testInvalidStudentOrgIdNotInList5() {
        assertFalse(ValidationUtil.isValidOrgId("555555555", "Student"));
    }

    @Test
    public void testValidFacultyOrgId5() {
        assertTrue(ValidationUtil.isValidOrgId("999999999", "Faculty"));
    }

    @Test
    public void testInvalidOrgIdNull5() {
        assertFalse(ValidationUtil.isValidOrgId(null));
        assertFalse(ValidationUtil.isValidOrgId(null, "Student"));
    }

    // ----------------------------------------------------------
    // REQUIREMENTS STRING TESTS
    // ----------------------------------------------------------

    @Test
    public void testEmailRequirements5() {
        assertEquals("Please enter a valid email address.", ValidationUtil.getEmailRequirements());
    }

    @Test
    public void testEmailRequirementsStudent5() {
        assertEquals("Student email must be in the format: username@my.yorku.ca",
                     ValidationUtil.getEmailRequirements("Student"));
    }

    @Test
    public void testPasswordRequirements5() {
        assertEquals("Password must be at least 8 characters and contain uppercase, lowercase, number, and special character.",
                     ValidationUtil.getPasswordRequirements());
    }

    @Test
    public void testOrgIdRequirementsGeneral5() {
        assertEquals("Organization ID must be exactly 9 characters.",
                     ValidationUtil.getOrgIdRequirements());
    }

    @Test
    public void testOrgIdRequirementsStudent5() {
        assertEquals("Student ID must be one of the valid registered student IDs.",
                     ValidationUtil.getOrgIdRequirements("Student"));
    }
}