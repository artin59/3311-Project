package Backend;
public abstract class Administrator extends Accounts {
	
	public Administrator(String email, String password) {
		super(email, password);
		this.setAccountStatus("ACTIVE");
	}

	
	//THESE ARE DONE IN THE GUI
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
