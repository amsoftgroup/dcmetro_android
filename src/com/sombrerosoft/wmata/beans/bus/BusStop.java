package com.sombrerosoft.wmata.beans.bus;

/*
 * WMATA Method 10: Bus Stops
 * 
 * http://developer.wmata.com/docs/read/Method_10
 * http://api.wmata.com/Bus.svc/Stops?lat=38.878586&lon=-76.989626&radius=500&api_key=YOUR_API_KEY
 * 
 */

import java.util.ArrayList;

public class BusStop {
	
	private float lat;
	private float lon;
	private String name;
	private ArrayList<String> routes;
	private float distanceFromMe;
	private float distanceFromMe2;
	private float distanceFromMe3;
	private float bearing;
	
	public BusStop(){
		routes = new ArrayList<String>();
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
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setRoutes(ArrayList<String> routes) {
		this.routes = routes;
	}
	public ArrayList<String> getRoutes() {
		return routes;
	}
	public void setDistanceFromMe(float distanceFromMe) {
		this.distanceFromMe = distanceFromMe;
	}
	public float getDistanceFromMe() {
		return distanceFromMe;
	}
	public void setDistanceFromMe2(float distanceFromMe2) {
		this.distanceFromMe2 = distanceFromMe2;
	}
	public float getDistanceFromMe2() {
		return distanceFromMe2;
	}
	public void setDistanceFromMe3(float distanceFromMe3) {
		this.distanceFromMe3 = distanceFromMe3;
	}
	public float getDistanceFromMe3() {
		return distanceFromMe3;
	}
	public void setBearing(float b) {
		bearing = b;
	}
	public float getBearing() {
		return bearing;
	}
}
