public class Student extends User {

	private String studentId;
	private final double HOURLY_RATE = 20.0;
	
	public Student(String email, String password, String studentId) {
		super(email, password);
		this.setStudentId(studentId);
		this.setVerified(false);
	}

	@Override
	public double getHourlyRate() {
		return HOURLY_RATE;
	}

	@Override
	public String getUserType() {
		return "Student";
	}

	@Override
	public boolean requiresVerfication() {
		return true;
	}

	protected String getStudentId() {
		return studentId;
	}

	protected void setStudentId(String studentId) {
		this.studentId = studentId;
	}

}
