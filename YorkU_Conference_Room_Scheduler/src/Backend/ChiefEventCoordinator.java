package Backend;

import java.util.Date;
import java.util.ArrayList;
import java.util.UUID;

public class ChiefEventCoordinator extends Administrator {

	private final static String CHIEF_EMAIL = "chief@gmail.com";
	private final static String CHIEF_PASS = "Chief1234$";
	
	private ArrayList<Admin> allAdmins = new ArrayList<>();
	
	private static ChiefEventCoordinator CEOInstance = new ChiefEventCoordinator(CHIEF_EMAIL, CHIEF_PASS);
	
	private ChiefEventCoordinator(String email, String password) {
		super(email, password);
	}
	
	public static ChiefEventCoordinator getCEOInstance() {
		return CEOInstance;
	}
	
	public Admin createAdmin(String email, String password) {
		Admin a = new Admin(email, password);
		allAdmins.add(a);
		return a;
	}
	
	// Convert existing user to admin
	public Admin convertUserToAdmin(Accounts account) {
	    if (account instanceof Admin) {
	        System.out.println("Account is already an Admin.");
	        return (Admin) account;
	    }
	    
	    if (account instanceof ChiefEventCoordinator) {
	        System.out.println("Cannot convert Chief Event Coordinator.");
	        return null;
	    }
	    
	    // Get the original data before creating new Admin
	    UUID originalId = account.getAccountId();
	    Date originalDate = account.getCreatedDate();
	    String email = account.getEmail();
	    String password = account.getPassword();
	    
	    // Create new Admin with same credentials
	    Admin newAdmin = new Admin(email, password);
	    
	    // Override the auto-generated values with original ones
	    newAdmin.setAccountId(originalId);
	    newAdmin.setCreatedDate(originalDate);
	    
	    allAdmins.add(newAdmin);
	    System.out.println("User " + email + " converted to Admin. ID: " + originalId);
	    return newAdmin;
	}
	
	public ArrayList<Admin> viewAllAdmins() {
		return allAdmins;
	}
	
	@Override
	public String getAccountType() {
		return "Chief Event Coordinator";
	}
}