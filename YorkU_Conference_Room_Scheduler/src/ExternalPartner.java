public class ExternalPartner extends User {

	private String partnerId;
	private final double HOURLY_RATE = 50.0;
	
	public ExternalPartner(String email, String password, String partnerId) {
		super(email, password);
		this.setPartnerId(partnerId);
		this.setVerified(true);	
		}

	@Override
	public double getHourlyRate() {
		return HOURLY_RATE;
	}

	@Override
	public String getUserType() {
		return "External Partner";
	}

	@Override
	public boolean requiresVerfication() {
		return false;
	}

	protected String getPartnerId() {
		return partnerId;
	}

	protected void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

}
