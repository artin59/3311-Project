package Backend;

import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern STUDENT_EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@my\\.yorku\\.ca$");
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int ORG_ID_LENGTH = 9;
    
    // Predefined valid student IDs
    private static final Set<String> VALID_STUDENT_IDS = new HashSet<>(Arrays.asList(
        "123456789",
        "987654321",
        "111111111",
        "222222222",
        "333333333"
    ));

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // Validate email based on account type
    public static boolean isValidEmail(String email, String accountType) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // General email format check first
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }
        
        // Student emails must be @my.yorku.ca
        if (accountType != null && accountType.equals("Student")) {
            return STUDENT_EMAIL_PATTERN.matcher(email).matches();
        }
        
        // Other account types can use any valid email
        return true;
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }

    public static boolean isValidOrgId(String orgId) {
        if (orgId == null) {
            return false;
        }
        return orgId.length() == ORG_ID_LENGTH;
    }
    
    // Validate Org ID based on account type
    public static boolean isValidOrgId(String orgId, String accountType) {
        if (orgId == null) {
            return false;
        }
        
        // Check length first
        if (orgId.length() != ORG_ID_LENGTH) {
            return false;
        }
        
        // Students must use one of the predefined IDs
        if (accountType != null && accountType.equals("Student")) {
            return VALID_STUDENT_IDS.contains(orgId);
        }
        
        // Other account types just need correct length
        return true;
    }

    public static String getPasswordRequirements() {
        return "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character.";
    }

    public static String getOrgIdRequirements() {
        return "Organization ID must be exactly 9 characters.";
    }
    
    public static String getOrgIdRequirements(String accountType) {
        if (accountType != null && accountType.equals("Student")) {
            return "Student ID must be one of the valid registered student IDs.";
        }
        return "Organization ID must be exactly 9 characters.";
    }

    public static String getEmailRequirements() {
        return "Please enter a valid email address.";
    }
    
    public static String getEmailRequirements(String accountType) {
        if (accountType != null && accountType.equals("Student")) {
            return "Student email must be in the format: username@my.yorku.ca";
        }
        return "Please enter a valid email address.";
    }
}