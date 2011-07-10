package com.sombrerosoft.wmata.beans.metro;

public class ElevatorIncident {

	private String dateOutOfServ;
    private String dateUpdated;
    private int displayOrder; 		//display priority(nfi?)
    private String LocationDescription;
    private String stationCode;
    private String stationName;
    private int symptomCode;
    private String symptomDescription;
    private int timeOutOfService;  	//min out of service since "dateUpdated"
    private String unitName;
    private String unitStatus; 		//can be "C" or "O". O = Out of service (has open issues). C = Operational (open issues were closed).
    private String unitType;		//"ELEVATOR" or "ESCALATOR"
    
	public String getDateOutOfServ() {
		return dateOutOfServ;
	}
	public void setDateOutOfServ(String dateOutOfServ) {
		this.dateOutOfServ = dateOutOfServ;
	}
	public String getDateUpdated() {
		return dateUpdated;
	}
	public void setDateUpdated(String dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	public int getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	public String getLocationDescription() {
		return LocationDescription;
	}
	public void setLocationDescription(String locationDescription) {
		LocationDescription = locationDescription;
	}
	public String getStationCode() {
		return stationCode;
	}
	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public int getSymptomCode() {
		return symptomCode;
	}
	public void setSymptomCode(int symptomCode) {
		this.symptomCode = symptomCode;
	}
	public String getSymptomDescription() {
		return symptomDescription;
	}
	public void setSymptomDescription(String symptomDescription) {
		this.symptomDescription = symptomDescription;
	}
	public int getTimeOutOfService() {
		return timeOutOfService;
	}
	public void setTimeOutOfService(int timeOutOfService) {
		this.timeOutOfService = timeOutOfService;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getUnitStatus() {
		return unitStatus;
	}
	public void setUnitStatus(String unitStatus) {
		this.unitStatus = unitStatus;
	}
	public String getUnitType() {
		return unitType;
	}
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

    
}
