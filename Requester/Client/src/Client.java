import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	Socket requestSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	Scanner input;

	// Constructor
	Client() {
		input = new Scanner(System.in);
	}

	void run() {
		try {
			// Creating a socket to connect to the server
			requestSocket = new Socket("127.0.0.1", 2004);
			System.out.println("Connected to localhost in port 2004");

			// Setup Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());

			boolean exit = false;
			boolean loggedIn = false; // Track whether the user is logged in

			do {
				// Display menu from server
				message = (String) in.readObject();
				System.out.println(message);

				// Get user input
				message = input.nextLine().trim();

				if (message.isEmpty()) {
					System.out.println("Invalid choice. Please enter a valid option.");
					continue; // Skip sending and go back to the menu
				}

				// Send message to server
				sendMessage(message);

				if (!loggedIn) {
					// Handle main menu options
					switch (message) {
					case "1": // Login
						loggedIn = handleLogin();
						break;

					case "2": // Register
						handleRegistration();
						break;

					case "-1": // Exit
						exit = true;
						System.out.println("Exiting the application.");
						break;

					default: // Invalid choice
						message = (String) in.readObject();
						System.out.println(message);
						break;
					}
				} else {
					// Handle logged-in menu options
					switch (message) {
					// Creating a Health and Safety Report
					case "1": createHealthAndSafetyReport(); break;
                    case "2": retrieveAccidentReports(); break;
                    case "3": assignReport(); break;
                    case "4": viewMyReports(); break;
                    case "5": updatePassword(); break;
					case "6":
						System.out.println("Logging out...");
						loggedIn = false;
						exit = true;
						break;
					default: // Invalid choice
						message = (String) in.readObject();
						System.out.println(message);
						break;
					}
				}
			} while (!exit);

		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (ClassNotFoundException classNotFound) {
			System.err.println("Data received in unknown format");
		} finally {
			closeConnection();
		}
	}

    // Send a message to the server
	void sendMessage(String msg) {
		try {
			out.writeObject(msg); // send the message to the server
			out.flush();
			// System.out.println("client: " + msg); // debugging
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// Menu 1 Option 1. Handle Login
	private boolean handleLogin() throws IOException, ClassNotFoundException {
		boolean loggedIn = false;

		while (!loggedIn) {
			// Email prompt from server
			message = (String) in.readObject();
			System.out.println(message);
			String email = input.nextLine();
			sendMessage(email);

			// Password prompt from server
			message = (String) in.readObject();
			System.out.println(message);
			String password = input.nextLine();
			sendMessage(password);

			// Login result from server
			message = (String) in.readObject();
			System.out.println(message);
			if (message.startsWith("Login successful")) {
				loggedIn = true;
			}
		}
		return loggedIn;
	}

	// Menu 1 Option 2. Handle Registration
	private void handleRegistration() throws IOException, ClassNotFoundException {
		// Name
		message = (String) in.readObject();
		System.out.println(message);
		message = input.nextLine();
		sendMessage(message);

		// Employee ID no
		boolean validEmployeeID = false;
		while (!validEmployeeID) {
			message = (String) in.readObject(); // Request from server
			System.out.println(message);
			String employeeIDInput = input.nextLine(); // Get user input
			sendMessage(employeeIDInput); // Send input to server

			// Validation response from server
			message = (String) in.readObject();
			System.out.println(message);

			if (!message.contains("already in use")) {
				validEmployeeID = true; // Employee ID is valid; exit the loop
			}
		}

		// Email
		message = (String) in.readObject();
		System.out.println(message);
		message = input.nextLine();
		sendMessage(message);

		// Password
		message = (String) in.readObject();
		System.out.println(message);
		message = input.nextLine();
		sendMessage(message);

		// Department Name
		message = (String) in.readObject();
		System.out.println(message);
		message = input.nextLine();
		sendMessage(message);

		// Role
		message = (String) in.readObject();
		System.out.println(message);
		message = input.nextLine();
		sendMessage(message);

		// Final registration confirmation
		message = (String) in.readObject();
		System.out.println(message);
	}

	// Menu 2 Option 1. Create a health and safety report
	private void createHealthAndSafetyReport() throws IOException, ClassNotFoundException {
		// Report type
		message = (String) in.readObject();
		System.out.println(message); // Report Type prompt
		String reportType = input.nextLine();
		sendMessage(reportType);

		message = (String) in.readObject();
		System.out.println(message); // Date prompt
		String reportDate = input.nextLine();
		sendMessage(reportDate);

		// Random report ID
		message = (String) in.readObject();
		System.out.println(message);

		// Created by
		message = (String) in.readObject();
		System.out.println(message);
	}

	// Menu 2 Option 2: Retrieve all accident reports
	private void retrieveAccidentReports() throws IOException, ClassNotFoundException {
		System.out.println("Retrieving all Accident Reports...");

		while (true) {
			message = (String) in.readObject();
			if (message.equals("END_OF_REPORTS")) {
				break;
			}
			System.out.println(message);
		}
	}

	// Menu 2 Option 3. Assign health & safety report (by report ID)
	private void assignReport() throws IOException, ClassNotFoundException {
		message = (String) in.readObject();
		System.out.println(message);
		String reportId = input.nextLine();
		sendMessage(reportId);

		message = (String) in.readObject();
		System.out.println(message);
		String employeeId = input.nextLine();
		sendMessage(employeeId);

		message = (String) in.readObject();
		System.out.println(message);
	}

	// Menu 2 Option 4. View reports assigned to me
	private void viewMyReports() throws IOException, ClassNotFoundException {
		System.out.println("Retrieving all reports assigned to you...");

		while (true) {
			// Read messages from the server
			String response = (String) in.readObject();

			// Break the loop if the server indicates the end of the report list
			if ("END_OF_REPORTS".equals(response)) {
				break;
			}

			// Display each report line sent by the server
			System.out.println(response);
		}
		//System.out.println("DEBUG: Returning to the main menu...");
	}

	// Menu 2 Option 5. Update Password
	private void updatePassword() throws IOException, ClassNotFoundException {
		// Request current password from the server
		message = (String) in.readObject();
		System.out.println(message);
		String currentPassword = input.nextLine();
		sendMessage(currentPassword);

		// Response from server
		message = (String) in.readObject();
		System.out.println(message);

		if (message.startsWith("Enter your new password:")) {
			// Send the new password
			String newPassword = input.nextLine();
			sendMessage(newPassword);

			// Confirmation from server
			message = (String) in.readObject();
			System.out.println(message);
		} else {
			System.out.println("Please try again.");
		}
	}
    
	// Close all connections
	private void closeConnection() {
		// Closing connection
		try {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (requestSocket != null)
				requestSocket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
    
	// Main method to start the client application
	public static void main(String args[]) {
		Client client = new Client();
		client.run();
	}
}