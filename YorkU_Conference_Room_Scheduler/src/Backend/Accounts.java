package Backend;

import java.util.Date;
import java.util.UUID;

public abstract class Accounts {

	private UUID accountId;
	private String email;
	private String password;
	private String accountStatus; //ACTIVE, or PENDING_VERIFICATION
	private Date createdDate;
	
	
	public abstract String getAccountType();

	
	public Accounts(String email, String password) {
		this.email = email;
		this.password = password;
		createdDate = new Date();
		accountId = UUID.randomUUID();
		accountStatus = "PENDING_VERIFICATION";
	}
	
	
	protected UUID getAccountId() {
		return accountId;
	}	
	protected void setAccountId(UUID accountId) {
		this.accountId = accountId;
	}
	public String getEmail() {
		return email;
	}
	protected void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
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
