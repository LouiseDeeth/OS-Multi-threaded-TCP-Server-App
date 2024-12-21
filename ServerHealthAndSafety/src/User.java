public class User {
	private final String name, email, departmentName, role;
	private final int employeeID;
	private String password;

	public User(String name, int employeeID, String email, String password, String departmentName, String role) {
		this.name = name;
		this.employeeID = employeeID;
		this.email = email;
		this.password = password;
		this.departmentName = departmentName;
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return name + "-" + employeeID + "-" + email + "-" + password + "-" + departmentName + "-" + role;
	}
}
