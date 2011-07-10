package com.sombrerosoft.wmata.beans.metro;

public class RailIncident {

	private String dateUpdated;
    private String delaySeverity;
    private String description;
    private String emergencyText; 			// nullable
    private String endLocationFullName; 	//nullable
    private int incidentID;
    private String incidentType;
    private String[] linesAffected;			//separated by semicolon
    private int passengerDelay;			// delay in minutes
    private String startLocationFullName;
 /*
	* IncidentID - ID of the incident
    * IncidentType - Type of the incident
    * DateUpdated - Date and time where information was updated.
    * DelaySeverity - Severity of delay (if any). Can be "Minor", "Major", "Medium".
    * Description - Description what happened.
    * EmergencyText - Some text for emergency (if any).
    * StartLocationFullName - Station where delay starts.
    * EndLocationFullName - Station where delay ends. If null, then incident belongs only to StartLocationFullName station.
    * LinesAffected - List of lines affected by the incident. Separated by ";" Example: "RD; YL; BL;"
    * PassengerDelay - Delay in minutes.
*/
    

	public String getDateUpdated() {
		return dateUpdated;
	}
	public void setDateUpdated(String dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	public String getDelaySeverity() {
		return delaySeverity;
	}
	public void setDelaySeverity(String delaySeverity) {
		this.delaySeverity = delaySeverity;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEmergencyText() {
		return emergencyText;
	}
	public void setEmergencyText(String emergencyText) {
		this.emergencyText = emergencyText;
	}
	public String getEndLocationFullName() {
		return endLocationFullName;
	}
	public void setEndLocationFullName(String endLocationFullName) {
		this.endLocationFullName = endLocationFullName;
	}
	public int getIncidentID() {
		return incidentID;
	}
	public void setIncidentID(int incidentID) {
		this.incidentID = incidentID;
	}
	public String getIncidentType() {
		return incidentType;
	}
	public void setIncidentType(String incidentType) {
		this.incidentType = incidentType;
	}
	public String[] getLinesAffected() {
		return linesAffected;
	}
	public void setLinesAffected(String[] linesAffected) {
		this.linesAffected = linesAffected;
	}
	public int getPassengerDelay() {
		return passengerDelay;
	}
	public void setPassengerDelay(int passengerDelay) {
		this.passengerDelay = passengerDelay;
	}
	public String getStartLocationFullName() {
		return startLocationFullName;
	}
	public void setStartLocationFullName(String startLocationFullName) {
		this.startLocationFullName = startLocationFullName;
	}

     
}
