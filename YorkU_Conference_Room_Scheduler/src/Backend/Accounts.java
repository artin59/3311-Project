package Backend;

import java.util.Date;
import java.util.UUID;

public abstract class Accounts {
	private UUID accountId;
	private String email;
	private String password;
	private String accountStatus; // ACTIVE, or PENDING_VERIFICATION
	private Date createdDate;
	
	public abstract String getAccountType();
	
	public Accounts(String email, String password) {
		this.email = email;
		this.password = password;
		createdDate = new Date();
		accountId = UUID.randomUUID();
		accountStatus = "PENDING_VERIFICATION";
	}
	
	// Getters
	public UUID getAccountId() {
		return accountId;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getAccountStatus() {
		return accountStatus;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}
	
	// Setters
	protected void setAccountId(UUID accountId) {
		this.accountId = accountId;
	}
	
	protected void setEmail(String email) {
		this.email = email;
	}
	
	protected void setPassword(String password) {
		this.password = password;
	}
	
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}