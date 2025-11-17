public class UserFactory {

	public User createUser(String email, String password, String type, String orgId) {
		
		if (type == "Student")
			return new Student(email, password, orgId);
		else if (type == "Staff")
			return new Staff(email, password, orgId);
		else if (type == "Faculty")
			return new Faculty(email, password, orgId);
		else if (type == "External Partner")
			return new ExternalPartner(email, password, orgId);
		else
			throw new NullPointerException("Type can't be null");
		
	}
	
}
