package com.sombrerosoft.wmata.beans.metro;

public class StationEntrance {

	private String description;
    private int id; 
    private float lat;
    private float lon;
    private String name;
    private String stationCode1;
    private String stationCode2;
    
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStationCode1() {
		return stationCode1;
	}
	public void setStationCode1(String stationCode1) {
		this.stationCode1 = stationCode1;
	}
	public String getStationCode2() {
		return stationCode2;
	}
	public void setStationCode2(String stationCode2) {
		this.stationCode2 = stationCode2;
	}
    
}
