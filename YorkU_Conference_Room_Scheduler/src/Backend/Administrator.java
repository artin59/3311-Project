package Backend;
public abstract class Administrator extends Accounts {
	
	public Administrator(String email, String password) {
		super(email, password);
		this.setAccountStatus("ACTIVE");
	}

	public void addRoom() {
		//TODO
	}
	
	public void enableRoom() {
		//TODO
	}
	
	public void disableRoom() {
		//TODO
	}
}
