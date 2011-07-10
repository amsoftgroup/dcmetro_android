package com.sombrerosoft.wmata.beans.bus;

import java.util.ArrayList;

public class RouteScheduleInfo  {

	/*
	 * WMATA Method 11: Bus Schedule by Route
	 * 
	 * http://developer.wmata.com/docs/read/Method_11
	 * http://api.wmata.com/Bus.svc/RouteSchedule?routeId=16L&date=2010-10-26&includingVariations=true&api_key=YOUR_API_KEY
	 */
	
	private String directionNum;
	private String endTime;
	private String routeID;
	private String startTime;
	private ArrayList<BusScheduleStopTime> stopTimes;
	private String tripDirectionText;
	private String tripHeadsign;
	private String tripID;
	
	public void setDirectionNum(String directionNum) {
		this.directionNum = directionNum;
	}
	public String getDirectionNum() {
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
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStopTimes(ArrayList<BusScheduleStopTime> stopTimes) {
		this.stopTimes = stopTimes;
	}
	public ArrayList<BusScheduleStopTime> getStopTimes() {
		return stopTimes;
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
