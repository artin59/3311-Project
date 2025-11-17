import java.util.Date;
import java.util.UUID;

abstract class Accounts {

	private UUID accountId;
	private String email;
	private String password;
	private String accountStatus; //ACTIVE, or PENDING_VERIFICATION
	private Date createdDate;
	
	
	
	
	public Accounts(String email, String password) {
		this.email = email;
		this.password = password;
		createdDate = new Date();
		accountId = UUID.randomUUID();
		accountStatus = "PENDING_VERIFICATION";
	}
	

	public boolean login() {
		
		//TODO
		return false;
	}
	
	public void logout() {
		//TODO
	}
	
	
	protected UUID getAccountId() {
		return accountId;
	}	
	protected void setAccountId(UUID accountId) {
		this.accountId = accountId;
	}
	protected String getEmail() {
		return email;
	}
	protected void setEmail(String email) {
		this.email = email;
	}
	protected String getPassword() {
		return password;
	}
	protected void setPassword(String password) {
		this.password = password;
	}
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
}
