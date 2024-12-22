import java.io.*;
import java.util.*;

public class UserList {
	private final List<User> list; // List to store all users

	// Constructor: Initializes the user list and loads data from the file.
	public UserList() {
		list = new ArrayList<>();
		loadUsersFromFile();
		//System.out.println("DEBUG: All users in memory: " + list);
	}

	private void loadUsersFromFile() {
		try (BufferedReader br = new BufferedReader(new FileReader("UserList.txt"))) {
			String fileContents;
			while ((fileContents = br.readLine()) != null) {
				// Parse each line to create a User object
				String[] parts = fileContents.split("-"); // Using - as delimiter 
				if (parts.length == 6) {// Ensure all fields are present
					String name = parts[0].trim();
					int employeeID = Integer.parseInt(parts[1].trim());
					String email = parts[2].trim();
					String password = parts[3].trim();
					String departmentName = parts[4].trim();
					String role = parts[5].trim();
					list.add(new User(name, employeeID, email, password, departmentName, role));
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("User list file not found. A new one will be created.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void addUser(User newUser) {
		list.add(newUser);
		saveUsersToFile();
	}

	public synchronized void updateUserPassword(User updatedUser) {
		// Find the user by email and update the password
		for (User user : list) {
			if (user.getEmail().equals(updatedUser.getEmail())) {
				user.setPassword(updatedUser.getPassword());
				break;
			}
		}
		saveUsersToFile(); // Save the updated list back to the file
	}

	private void saveUsersToFile() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("UserList.txt"))) {
			for (User user : list) {
				bw.write(user.toString());
				bw.newLine();
			}
			System.out.println("User list updated successfully.");
		} catch (IOException e) {
			System.err.println("Error writing to user list file.");
			e.printStackTrace();
		}
	}

	// Finds a user by email and password.
	public synchronized Optional<User> findUserByEmailAndPassword(String email, String password) {
		return list.stream().filter(user -> user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)).findFirst();
	}

	// Checks if a given email or employee ID already exists in the system.
	public synchronized boolean isEmailOrEmployeeIDExists(String email, int employeeID) {
		return list.stream().anyMatch(user -> user.getEmail().equals(email) || user.getEmployeeID() == employeeID);
	}
}
