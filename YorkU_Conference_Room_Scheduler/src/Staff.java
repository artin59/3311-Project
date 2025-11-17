public class Staff extends User {
	
	private String staffId;
	private final double HOURLY_RATE = 40.0;
	
	public Staff(String email, String password, String staffId) {
		super(email, password);
		this.setStaffId(staffId);
		this.setVerified(true);	
	}

	@Override
	public double getHourlyRate() {
		return HOURLY_RATE;
	}

	@Override
	public String getUserType() {
		return "Staff";
	}

	@Override
	public boolean requiresVerfication() {
		return false;
	}

	protected String getStaffId() {
		return staffId;
	}

	protected void setStaffId(String staffId) {
		this.staffId = staffId;
	}

}
