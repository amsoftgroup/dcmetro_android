package com.sombrerosoft.wmata.beans.bus;

public class StopScheduleArrival {
	
/*
 * WMATA Method 14: Bus Schedule by Stop
 * 
 * http://developer.wmata.com/docs/read/Method_14
 * http://api.wmata.com/Bus.svc/StopSchedule?stopId=2000019&date=2010-12-01&api_key=YOUR_API_KEY
 */

	private int directionNum;
	private String endTime;
	private String routeID;
	private String scheduleTime;
	private String startTime;
	private String tripDirectionText;
	private String tripHeadsign;
	private String tripID;
	
	public void setDirectionNum(int directionNum) {
		this.directionNum = directionNum;
	}
	public int getDirectionNum() {
		return directionNum;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}
	public String getRouteID() {
		return routeID;
	}
	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	public String getScheduleTime() {
		return scheduleTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setTripDirectionText(String tripDirectionText) {
		this.tripDirectionText = tripDirectionText;
	}
	public String getTripDirectionText() {
		return tripDirectionText;
	}
	public void setTripHeadsign(String tripHeadsign) {
		this.tripHeadsign = tripHeadsign;
	}
	public String getTripHeadsign() {
		return tripHeadsign;
	}
	public void setTripID(String tripID) {
		this.tripID = tripID;
	}
	public String getTripID() {
		return tripID;
	}
	
	
}
