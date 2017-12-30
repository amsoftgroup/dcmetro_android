package com.sombrerosoft.wmata.beans.metro;

public class Station {

	private String code;
	private float lat;
	private float lon;
	private String lineCode1;
	private String lineCode2;
	private String lineCode3;
	private String lineCode4;
	private String name;
	private String stationTogether1;
	private String stationTogether2;
	private float distanceFromMe;
	private float distanceFromMe2;
	private float distanceFromMe3;
	private float bearingTo;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLon() {
		return lon;
	}
	public void setLon(float lon) {
		this.lon = lon;
	}
	public String getLineCode1() {
		return lineCode1;
	}
	public void setLineCode1(String lineCode1) {
		this.lineCode1 = lineCode1;
	}
	public String getLineCode2() {
		return lineCode2;
	}
	public void setLineCode2(String lineCode2) {
		this.lineCode2 = lineCode2;
	}
	public String getLineCode3() {
		return lineCode3;
	}
	public void setLineCode3(String lineCode3) {
		this.lineCode3 = lineCode3;
	}
	public String getLineCode4() {
		return lineCode4;
	}
	public void setLineCode4(String lineCode4) {
		this.lineCode4 = lineCode4;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStationTogether1() {
		return stationTogether1;
	}
	public void setStationTogether1(String stationTogether1) {
		this.stationTogether1 = stationTogether1;
	}
	public String getStationTogether2() {
		return stationTogether2;
	}
	public void setStationTogether2(String stationTogether2) {
		this.stationTogether2 = stationTogether2;
	}
	
	@Override 
	public String toString(){
		return name;
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
	public void setBearingTo(float bearingTo) {
		this.bearingTo = bearingTo;
	}
	public float getBearingTo() {
		return bearingTo;
	}

}
