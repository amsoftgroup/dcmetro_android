package com.sombrerosoft.wmata.beans.bus;

public class Route {

	/*
	 * WMATA method #9: Bus Routes
	 * 
	 * http://developer.wmata.com/docs/read/Method_9
	 * http://api.wmata.com/Bus.svc/Routes?api_key=YOUR_API_KEY 
	 */
	
	private String name;
	private String routeID;

	
	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}
	public String getRouteID() {
		return routeID;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
}
