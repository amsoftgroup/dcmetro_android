package com.sombrerosoft.android.wmata;

import java.util.ArrayList;


import com.sombrerosoft.android.wmata.dal.XmlPullFeedParser;
import com.sombrerosoft.android.wmata.helper.AppHelper;
import com.sombrerosoft.wmata.beans.*;
import com.sombrerosoft.wmata.beans.metro.RailIncident;

import android.app.Application;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;


public class WMATAApp extends Application{
	
	private Location loc;
	private double lat;
	private double lon;
	private ArrayList<RailIncident> railIncidents;

	public void setRailIncidents(ArrayList<RailIncident> railIncidents) {
		this.railIncidents = railIncidents;
	}

    public ArrayList<RailIncident> getRailIncidents(){
    	return railIncidents;
    }
    /*
	public void setLocation(Location loc) {
		this.loc = loc;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLat() {
		return lat;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLon() {
		return lon;
	}
*/
}
