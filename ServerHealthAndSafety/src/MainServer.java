import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer {

	public static void main(String args[]) {
		int port = 2004;
		List<User> users = Collections.synchronizedList(new ArrayList<>()); // Library of Users
		List<HealthAndSafetyReports> reports = Collections.synchronizedList(new ArrayList<>()); // library of H&S

		try {
			// start the server
			ServerSocket providerSocket = new ServerSocket(port, 10);
			System.out.println("Server is running on port " + port);

			while (true) {
				// accept client connections
				Socket connection = providerSocket.accept();

				// Handle client in a new thread
				ServerThread handler = new ServerThread(connection, users, reports);
				handler.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}