public class User {
	private final String name, email, departmentName, role;
	private final int employeeID;
	private String password;

	public User(String name, int employeeID, String email, String password, String departmentName, String role) {
		this.name = name; // User's full name
		this.employeeID = employeeID; // Unique employee ID
		this.email = email; // User's email address
		this.password = password; // User's password
		this.departmentName = departmentName; // Name of the department the user belongs to
		this.role = role; // Role of the user (e.g., Admin, Employee)
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
