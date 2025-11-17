public abstract class User extends Accounts{

	private boolean isVerified;
	
	public User(String email, String password) {
		super(email, password);
	}

	
	public abstract double getHourlyRate();
	public abstract String getUserType();
	public abstract boolean requiresVerfication();
	
	protected boolean isVerified() {
		return isVerified;
	}

	protected void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

}
