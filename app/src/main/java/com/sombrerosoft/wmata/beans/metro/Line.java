package com.sombrerosoft.wmata.beans.metro;

public class Line {

	private String displayName;
	private String endStationCode;
	private String startStationCode;
	private String internalDestination1;
	private String internalDestination2;
	private String lineCode;
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getEndStationCode() {
		return endStationCode;
	}
	public void setEndStationCode(String endStationCode) {
		this.endStationCode = endStationCode;
	}
	public String getStartStationCode() {
		return startStationCode;
	}
	public void setStartStationCode(String startStationCode) {
		this.startStationCode = startStationCode;
	}
	public String getInternalDestination1() {
		return internalDestination1;
	}
	public void setInternalDestination1(String internalDestination1) {
		this.internalDestination1 = internalDestination1;
	}
	public String getInternalDestination2() {
		return internalDestination2;
	}
	public void setInternalDestination2(String internalDestination2) {
		this.internalDestination2 = internalDestination2;
	}
	public String getLineCode() {
		return lineCode;
	}
	public void setLineCode(String lineCode) {
		this.lineCode = lineCode;
	}
	
	@Override 
	public String toString(){
		return displayName;
	}

    
}
