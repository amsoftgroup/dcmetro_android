package com.sombrerosoft.wmata.beans.bus;

public class ShapePoint {

/*
 * Used in WMATA Method 12: Bus Route Details
 * http://developer.wmata.com/docs/read/Method_12
 * http://api.wmata.com/Bus.svc/RouteDetails?routeId=16L&date=2010-12-08&api_key=YOUR_API_KEY
 * 
 * RouteDetailsInfo "has a" ShapePoint
 * 
*/
	
	private float lat;
	private float lon;
	private int seqNum;
	
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
	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}
	public int getSeqNum() {
		return seqNum;
	}
	
	
	
}
