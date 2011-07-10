package com.sombrerosoft.wmata.beans.bus;

/*
 * WMATA "Method 13": Bus Positions
 * http://developer.wmata.com/docs/read/Method_13
 * http://api.wmata.com/Bus.svc/BusPositions?routeId=10A&includingVariations=true&lat=38.878586&lon=-76.989626&radius=50000&api_key=YOUR_API_KEY
 */
		
public class BusPosition {

	private String dateTime;
	private float deviation;
	private int directionNum;
	private String directionText;
	private float lat;
	private float lon;
	private String routeID;
	private String tripHeadsign;
	private String tripID;
	private String tripStartTime;
	private String vehicleID;
	
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDeviation(float deviation) {
		this.deviation = deviation;
	}
	public float getDeviation() {
		return deviation;
	}
	public void setDirectionNum(int directionNum) {
		this.directionNum = directionNum;
	}
	public int getDirectionNum() {
		return directionNum;
	}
	public void setDirectionText(String directionText) {
		this.directionText = directionText;
	}
	public String getDirectionText() {
		return directionText;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLat() {
		return lat;
	}
	public void setLon(float lon) {
		this.lon = lon;
	}
	public float getLon() {
		return lon;
	}
	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}
	public String getRouteID() {
		return routeID;
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
	public void setTripStartTime(String tripStartTime) {
		this.tripStartTime = tripStartTime;
	}
	public String getTripStartTime() {
		return tripStartTime;
	}
	public void setVehicleID(String vehicleID) {
		this.vehicleID = vehicleID;
	}
	public String getVehicleID() {
		return vehicleID;
	}
	
}
