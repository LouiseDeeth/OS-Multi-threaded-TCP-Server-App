import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class HealthAndSafetyReportsList {
	private final List<HealthAndSafetyReports> list;

	public HealthAndSafetyReportsList() {
		list = Collections.synchronizedList(new LinkedList<>());
		loadReportsFromFile();
	}

	private void loadReportsFromFile() {
		try (BufferedReader br = new BufferedReader(new FileReader("HealthAndSafetyReports.txt"))) {
			String fileContents;
			while ((fileContents = br.readLine()) != null) {
				//System.out.println("DEBUG: Reading line - " + fileContents);
				String[] parts = fileContents.split("\\|"); // Use | as the delimiter
				
				if (parts.length >= 6) { // Ensure all fields are present
					try {
						String type = parts[0].trim();
						String reportID = parts[1].trim();
						String date = parts[2].trim();
						int createdBy = Integer.parseInt(parts[3].trim());
						int assignedTo = Integer.parseInt(parts[4].trim());
						String status = parts[5].trim();

						HealthAndSafetyReports report = new HealthAndSafetyReports(type, reportID, date, createdBy);
						report.setAssignedTo(assignedTo);
						report.setStatus(status);

						//System.out.println("DEBUG: Parsed report - " + report);
						list.add(report);
					} catch (NumberFormatException e) {
						System.err.println("Skipping invalid report line (NumberFormatException): " + fileContents);
					}
				} else {
					System.err.println("Skipping incomplete report line: " + fileContents);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("HealthAndSafetyReports file not found. A new one will be created.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void addReport(String type, String reportID, String date, int createdBy) {
		HealthAndSafetyReports newReport = new HealthAndSafetyReports(type, reportID, date, createdBy);
		list.add(newReport); // Add the report to the list
		saveReportsToFile(); // Persist the updated list
	}

	public void saveReportsToFile() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("HealthAndSafetyReports.txt"))) {
			for (HealthAndSafetyReports report : list) {
				// Use | as the delimiter
				bw.write(String.join("|", report.getType(), report.getReportID(), report.getDate(),
						String.valueOf(report.getCreatedBy()), String.valueOf(report.getAssignedTo()), 
						report.getStatus()));
				bw.newLine(); // Add a new line after each report
			}
			System.out.println("Reports file updated successfully.");
		} catch (IOException e) {
			System.err.println("Error writing to reports file.");
			e.printStackTrace();
		}
	}

	// Retrieve all Accident Reports
	public synchronized List<HealthAndSafetyReports> retrieveAccidentReports() {
		return list.stream().filter(report -> report.getType().trim().equalsIgnoreCase("Accident Report")).collect(Collectors.toList()); // Filter the list for accident reports
	}

	// Find a report by ID
	public synchronized Optional<HealthAndSafetyReports> findReportByID(String reportID) {
		return list.stream().filter(report -> report.getReportID().equalsIgnoreCase(reportID)).findFirst(); // Search for the report by ID
	}

	// Retrieve all reports assigned to a specific user by Employee ID
	public synchronized List<HealthAndSafetyReports> getReportsAssignedToUser(int employeeID) {
		return list.stream().filter(report -> report.getAssignedTo() == employeeID).collect(Collectors.toList()); // Filter the list for reports assigned to the given employee
	}
}
