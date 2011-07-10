package com.sombrerosoft.wmata.beans.metro;

public class RailPath {

	private int distanceToPrev; // in feet
    private String lineCode;
    private int seqNum;
    private String stationCode;
    private String stationName;
	public int getDistanceToPrev() {
		return distanceToPrev;
	}
	public void setDistanceToPrev(int distanceToPrev) {
		this.distanceToPrev = distanceToPrev;
	}
	public String getLineCode() {
		return lineCode;
	}
	public void setLineCode(String lineCode) {
		this.lineCode = lineCode;
	}
	public int getSeqNum() {
		return seqNum;
	}
	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
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

    
}
