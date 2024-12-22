import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {
	private final Socket connection; // Client connection socket
	private final List<User> users; // Shared list of registered users
	private final List<HealthAndSafetyReports> reports; // Shared list of health and safety reports
	private ObjectOutputStream out; // Output stream to the client
	private ObjectInputStream in; // Input stream from the client

	// Constructor: Initialize client connection and shared resources
	public ServerThread(Socket connection, List<User> users, List<HealthAndSafetyReports> reports) {
		this.connection = connection;
		this.users = users;
		this.reports = reports;
	}

	@Override
	public void run() {
		try {
			// Input and output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());

			boolean loggedIn = false; // Tracks if the user is logged in
			User currentUser = null; // Holds the current user's data

			do {
				// Welcome message
				sendMessage("*** Welcome to the Health & Safety Reporting System ***\nPlease choose an option\n1. Log-In\n2. Register\n-1. Exit");
				String choice = (String) in.readObject();

				switch (choice) {
				case "1": // Login
					currentUser = handleLogin(); // Assign the logged-in user
					loggedIn = true;
					break;

				case "2": // Register
					handleRegistration();
					loggedIn = false;
					break;

				case "-1": // Exit
					sendMessage("Goodbye!");
					return;

				default: // Invalid choice
					sendMessage("Invalid choice. Please enter 1, 2, or -1.");
					break;
				}
			} while (!loggedIn);

			// Menu 2: logged in options
			while (loggedIn) {
				sendMessage("\nChoose an option:\n1. Create a Health and Safety Report\n2. Retrieve all Accident Reports\n3. Assign a health & safety Report\n4. View all reports assigned to me\n5. Update password\n6. Exit\n");
				String choice = (String) in.readObject();

				switch (choice) {
				case "1":
					createHealthAndSafetyReport(currentUser);
					break;
				case "2":
					retrieveAccidentReports();
					break;
				case "3":
					assignReport();
					break;
				case "4":
					viewMyReports(currentUser);
					break;
				case "5":
					updatePassword(currentUser);
					break;
				case "6": // Exit
					sendMessage("Goodbye, " + currentUser.getName() + "!");
					loggedIn = false;
					break;
				default: // Invalid choice
					sendMessage("Invalid choice. Please enter 1 to 6.");
					break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			// Closing connection
			closeConnection();
		}
	}

	// Menu 1 Option 1. Handle Login
	private User handleLogin() throws IOException, ClassNotFoundException {
		UserList userList = new UserList();
		User currentUser = null;

		while (currentUser == null) { // Keep looping until login is successful
			sendMessage("Enter Email:");
			String email = (String) in.readObject(); // Receive email from the client

			sendMessage("Enter Password:");
			String password = (String) in.readObject(); // Receive password from the client

			Optional<User> user = userList.findUserByEmailAndPassword(email, password);

			if (user.isPresent()) {
				sendMessage("Login successful! Welcome back, " + user.get().getName());
				currentUser = user.get();
			} else {
				sendMessage("Invalid Email or Password. Please try again.");
			}
		}
		return currentUser; // Return the logged-in user
	}

	// Menu 1 Option 2. Handle Registration
	private void handleRegistration() throws IOException, ClassNotFoundException {
		sendMessage("Enter your Name:");
		String name = (String) in.readObject();

		int employeeID = -1;
		boolean validEmployeeID = false;
		UserList userList = new UserList();

		while (!validEmployeeID) {
			sendMessage("Enter your Employee ID:");
			String employeeIDInput = (String) in.readObject();
			try {
				employeeID = Integer.parseInt(employeeIDInput);
				if (userList.isEmailOrEmployeeIDExists(null, employeeID)) {
					sendMessage("Employee ID already in use. Please enter a different Employee ID.");
				} else {
					validEmployeeID = true; // Employee ID is valid
					sendMessage("Employee ID accepted."); // Inform client
				}
			} catch (NumberFormatException e) {
				sendMessage("Invalid input. Employee ID must be a number. Please try again.");
			}
		}

		sendMessage("Enter your Email:");
		String email = (String) in.readObject();

		sendMessage("Enter your Password:");
		String password = (String) in.readObject();

		sendMessage("Enter your Department Name:");
		String departmentName = (String) in.readObject();

		sendMessage("Enter your Role:");
		String role = (String) in.readObject();

		synchronized (users) {
			if (userList.isEmailOrEmployeeIDExists(email, -1)) {
				sendMessage(
						"Registration failed: Email is already in use. Please try registering with a different email.");
			} else {
				User newUser = new User(name, employeeID, email, password, departmentName, role);
				users.add(newUser);
				userList.addUser(newUser);
				sendMessage("Registration successful! Please log in.");
			}
		}
	}

	public class RandomNumberGenerator {
		public static String generate8DigitNumber() {
			Random random = new Random();
			int randomNumber = 10000000 + random.nextInt(90000000); // Generate number in range 10,000,000 to 99,999,999
			return String.valueOf(randomNumber);
		}
	}

	// Menu 2 Option 1. Create a health and safety report
	private void createHealthAndSafetyReport(User currentUser) throws IOException, ClassNotFoundException {
		if (currentUser == null) {
			sendMessage("Error: You must be logged in to create a report.");
			return;
		}

		// Get report type
		sendMessage("\nChoose an option:\n1: Create New Accident Report\n2: Create New Health & Safety Risk Report");
		String reportTypeChoice = (String) in.readObject();
		String type = reportTypeChoice.equals("1") ? "Accident Report" : "Health & Safety Risk Report";

		// Get report date
		sendMessage("Enter Report Date (YYYY-MM-DD):");
		String date = (String) in.readObject();

		// Generate unique report ID
		String reportID = RandomNumberGenerator.generate8DigitNumber();

		// Employee ID of report creator
		int createdBy = currentUser.getEmployeeID();

		// Default values
		String status = "Open";
		int assignedTo = 0; // Unassigned initially

		// Create a new report
		HealthAndSafetyReports newReport = new HealthAndSafetyReports(type, reportID, date, createdBy);
		newReport.setStatus(status);
		newReport.setAssignedTo(assignedTo);

		synchronized (reports) {
			reports.add(newReport);
		}

		HealthAndSafetyReportsList reportList = new HealthAndSafetyReportsList();
		reportList.addReport(type, reportID, date, createdBy);

		sendMessage("Report created successfully!");
		sendMessage("Report Details: " + newReport);
	}

	// Menu 2 Option 2. Retrieve all accident reports
	private void retrieveAccidentReports() throws IOException {
		HealthAndSafetyReportsList reportList = new HealthAndSafetyReportsList();
		List<HealthAndSafetyReports> accidentReports = reportList.retrieveAccidentReports();

		if (accidentReports.isEmpty()) {
			sendMessage("No accident reports found.");
		} else {
			sendMessage("Accident Reports:");
			for (HealthAndSafetyReports report : accidentReports) {
				sendMessage(report.toString());
			}
		}
		sendMessage("END_OF_REPORTS");
	}

	// Menu 2 Option 3: Assign a health & safety report
	private void assignReport() throws IOException, ClassNotFoundException {
		HealthAndSafetyReportsList reportList = new HealthAndSafetyReportsList();

		// Prompt for Report ID
		sendMessage("Enter the Report ID of the report you want to assign:");
		String reportId = (String) in.readObject();

		// Find the report by ID
		Optional<HealthAndSafetyReports> reportOptional = reportList.findReportByID(reportId);
		if (reportOptional.isEmpty()) {
			sendMessage("Error: Report ID not found.");
			return;
		}

		// Ask for Employee ID
		sendMessage("Enter the Employee ID to assign this report:");
		String employeeIdInput = (String) in.readObject();
		int employeeId;
		try {
			employeeId = Integer.parseInt(employeeIdInput);
		} catch (NumberFormatException e) {
			sendMessage("Error: Invalid Employee ID. It must be a number.");
			return;
		}

		// Update the report
		HealthAndSafetyReports report = reportOptional.get();
		report.setAssignedTo(employeeId);
		report.setStatus("Assigned");

		// Save the updated report back to the list and file
		reportList.saveReportsToFile();
		sendMessage("Report successfully assigned to Employee ID: " + employeeId);
	}

	// Menu 2 Option 4. View reports assigned to me
	private void viewMyReports(User currentUser) throws IOException {
		if (currentUser == null) {
			sendMessage("Error: You must be logged in to view assigned reports.");
			return;
		}

		HealthAndSafetyReportsList reportList = new HealthAndSafetyReportsList();
		List<HealthAndSafetyReports> assignedReports = reportList.getReportsAssignedToUser(currentUser.getEmployeeID());

		if (assignedReports.isEmpty()) {
			sendMessage("No reports assigned to you.");
		} else {
			sendMessage("Reports assigned to you:");
			for (HealthAndSafetyReports report : assignedReports) {
				sendMessage(report.toString());
			}
		}
		sendMessage("END_OF_REPORTS");
	}

	// Menu 2 Option 5. Update Password
	private synchronized boolean updatePassword(User currentUser) throws IOException, ClassNotFoundException {
		if (currentUser == null) {
			sendMessage("Error: No user is currently logged in. Please log in again.");
			return false;
		}

		UserList userList = new UserList();

		// Request current password
		sendMessage("Enter your current password:");
		String currentPassword = (String) in.readObject();

		// Verify the current password
		if (!currentPassword.equals(currentUser.getPassword())) {
			sendMessage("Incorrect current password.");
			return false;
		}

		// Request the new password
		sendMessage("Enter your new password:");
		String newPassword = (String) in.readObject();

		// Update the password
		currentUser.setPassword(newPassword);

		// Persist the updated password in UserList
		synchronized (userList) {
			userList.updateUserPassword(currentUser);
		}

		sendMessage("Password updated successfully!");
		return true;
	}

	// Sends a message to the client
	private void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("server>" + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	// Closes the connection and releases resources
	private void closeConnection() {
		try {
			in.close();
			out.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}