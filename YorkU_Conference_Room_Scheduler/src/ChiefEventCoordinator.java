import java.util.ArrayList;

public class ChiefEventCoordinator extends Administrator {

	private final static String CHIEF_EMAIL = "chief@gmail.com";
	private final static String CHIEF_PASS = "Chief1234$";
	
	private ArrayList<Admin> allAdmins= new ArrayList<>();
	
	private static ChiefEventCoordinator CEOInstance = new ChiefEventCoordinator(CHIEF_EMAIL, CHIEF_PASS);
	private ChiefEventCoordinator(String email, String password) {
		super(email, password);
	}
	public static ChiefEventCoordinator getCEOInstance() {
		return CEOInstance;
	}
	
	public Admin createAdmin (String email, String password) {
		Admin a = new Admin(email, password);
		allAdmins.add(a);
		return a;
	}
	
	public ArrayList<Admin> viewAllAdmins(){
		return allAdmins;
	}

}
