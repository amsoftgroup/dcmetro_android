package com.sombrerosoft.wmata.beans.bus;

/*
 * WMATA Method 12: Bus Route Details
 * http://developer.wmata.com/docs/read/Method_12
 * http://api.wmata.com/Bus.svc/RouteDetails?routeId=16L&date=2010-12-08&api_key=YOUR_API_KEY
 * 
 * "Has A" ShapePoint
 * "Has A" Stop
 */
import java.util.ArrayList;

public class RouteDetailsInfo {

	private String directionNum;
	private String directionText;
	private ArrayList<ShapePoint> shapePoints;
	private ArrayList<Stop> stops;
	private String tripHeadsign;
	private String name;
	private String routeID;
	
	public void setDirectionNum(String directionNum) {
		this.directionNum = directionNum;
	}
	public String getDirectionNum() {
		return directionNum;
	}
	public void setDirectionText(String directionText) {
		this.directionText = directionText;
	}
	public String getDirectionText() {
		return directionText;
	}
	public void setShapePoints(ArrayList<ShapePoint> shapePoints) {
		this.shapePoints = shapePoints;
	}
	public ArrayList<ShapePoint> getShapePoints() {
		return shapePoints;
	}
	public void setStops(ArrayList<Stop> stops) {
		this.stops = stops;
	}
	public ArrayList<Stop> getStops() {
		return stops;
	}
	public void setTripHeadsign(String tripHeadsign) {
		this.tripHeadsign = tripHeadsign;
	}
	public String getTripHeadsign() {
		return tripHeadsign;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}
	public String getRouteID() {
		return routeID;
	}
	
}
