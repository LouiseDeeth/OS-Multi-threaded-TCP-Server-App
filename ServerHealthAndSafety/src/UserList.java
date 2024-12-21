import java.io.*;
import java.util.*;

public class UserList {
	private final List<User> list;

	public UserList() {
		list = new ArrayList<>();
		loadUsersFromFile();
		System.out.println("DEBUG: All users in memory: " + list);
	}

	private void loadUsersFromFile() {
		try (BufferedReader br = new BufferedReader(new FileReader("UserList.txt"))) {
			String fileContents;
			while ((fileContents = br.readLine()) != null) {
				String[] parts = fileContents.split("-"); // Using - as delimiter based on User.toString()
				if (parts.length == 6) {
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
		// Update the password in the in-memory list
		for (User user : list) {
			if (user.getEmail().equals(updatedUser.getEmail())) {
				user.setPassword(updatedUser.getPassword());
				break;
			}
		}
		// Save the updated list back to the file
		saveUsersToFile();
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

	public synchronized Optional<User> findUserByEmailAndPassword(String email, String password) {
		return list.stream()
				.filter(user -> user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password))
				.findFirst();
	}

	public synchronized boolean isEmailOrEmployeeIDExists(String email, int employeeID) {
		return list.stream().anyMatch(user -> user.getEmail().equals(email) || user.getEmployeeID() == employeeID);
	}

}
