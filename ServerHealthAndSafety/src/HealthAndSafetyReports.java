import java.io.Serializable; 

//Implements Serializable to allow easy storage and transmission.
public class HealthAndSafetyReports implements Serializable {
	private static final long serialVersionUID = 1L;
	private String reportID, type, date;
	private int createdBy;
	private int assignedTo = 0; // default 0 for unassigned
	private String status = "Open"; // Initial status of the report

	public HealthAndSafetyReports(String type, String reportID, String date, int createdBy) {
		this.type = type; // Type of report (e.g., "Accident Report")
		this.reportID = reportID; // Unique ID for the report
		this.date = date; // Date of the report (YYYY-MM-DD)
		this.createdBy = createdBy; // ID number of the creator
	}

	// Getters and Setters
	public String getReportID() {
		return reportID;
	}

	public String getType() {
		return type;
	}

	public String getDate() {
		return date;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public int getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(int assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Type: " + type + ", Report ID: " + reportID + ", Date: " + date + ", Created By: " + createdBy
				+ ", Status: " + status + (assignedTo == 0 ? ", Unassigned" : ", Assigned To: " + assignedTo);
	}
}
