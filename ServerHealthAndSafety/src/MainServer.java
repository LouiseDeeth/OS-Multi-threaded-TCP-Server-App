import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer {
	
	public static void main(String args[])
	{
		ServerSocket providerSocket;
		Socket connection;
		ServerThread handler;
		int port = 2004;
		List<User> users = Collections.synchronizedList(new ArrayList<>()); // Library of Users
		List<HealthAndSafetyReports> reports = Collections.synchronizedList(new ArrayList<>()); //library of H&S Reports
		
		try {
			//start the server
			providerSocket = new ServerSocket(port, 10);
			System.out.println("Server is running on port " + port);
			
			while(true) {
				//accept client connections
				connection = providerSocket.accept();
				
				// Handle client in a new thread
				handler = new ServerThread(connection, users, reports);
				handler.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}