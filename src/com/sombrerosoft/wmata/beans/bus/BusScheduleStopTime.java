package com.sombrerosoft.wmata.beans.bus;

/*
 * used in "Method 11: Bus Schedule by Route"
 * 
 * RouteScheduleInfo "has a" BusScheduleStopTime
 * 
 * http://developer.wmata.com/docs/read/Method_11 
 * http://api.wmata.com/Bus.svc/RouteSchedule?routeId=16L&date=2010-10-26&includingVariations=true&api_key=YOUR_API_KEY 
 */


public class BusScheduleStopTime {
	
	private String stopID;
	private String stopName;
	private String stopSeq;
	private String time;
	
	public void setStopID(String stopID) {
		this.stopID = stopID;
	}
	public String getStopID() {
		return stopID;
	}
	public void setStopName(String stopName) {
		this.stopName = stopName;
	}
	public String getStopName() {
		return stopName;
	}
	public void setStopSeq(String stopSeq) {
		this.stopSeq = stopSeq;
	}
	public String getStopSeq() {
		return stopSeq;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return time;
	}
	
}
