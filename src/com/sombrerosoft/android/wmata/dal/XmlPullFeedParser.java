package com.sombrerosoft.android.wmata.dal;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;

import com.sombrerosoft.android.wmata.R;

import com.sombrerosoft.wmata.beans.bus.BusPosition;
import com.sombrerosoft.wmata.beans.bus.Route;
import com.sombrerosoft.wmata.beans.bus.RouteScheduleInfo;
import com.sombrerosoft.wmata.beans.bus.BusScheduleStopTime;
import com.sombrerosoft.wmata.beans.bus.BusStop;
import com.sombrerosoft.wmata.beans.bus.StopScheduleArrival;
import com.sombrerosoft.wmata.beans.metro.Line;
import com.sombrerosoft.wmata.beans.metro.RailIncident;
import com.sombrerosoft.wmata.beans.metro.RailStationPrediction;
import com.sombrerosoft.wmata.beans.metro.Station;

public class XmlPullFeedParser {

	private static XmlPullFeedParser xmlpp;
	private String key = "pssvrnd7sru72whv2vpze75j";
	//private String key = "dpqp9rgs7ws4rd5neknhzrm2";
	private String TAG = "XmlPullFeedParser";

	
	private XmlPullFeedParser() {
	
	}
	
	public static XmlPullFeedParser getXmlPullFeedParser(){
		if (xmlpp == null){
			xmlpp = new XmlPullFeedParser();
		}
		return xmlpp;
	}
	
	private String getKey(){
		return key;
	}

	private URL getURL(String url){
		URL feedUrl = null;
		try {
			feedUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e(this.toString(), "error: " + e.toString());
		}
		return feedUrl;	
	}

	private InputStream getInputStream(URL url){

		InputStream is = null;

		try {
			is = url.openConnection().getInputStream();
		} catch (IOException e) {
			Log.e(this.toString(), "error: " + e.toString());
		}
		return is;
	}

	public ArrayList<Line> parseLines() {

		String url = new StringBuffer().append("http://api.wmata.com/Rail.svc/Lines")
				.append("?api_key=").append(getKey()).toString(); 

		ArrayList<Line> lines = null;
		XmlPullParser parser = Xml.newPullParser();

		try {

			URL u = getURL(url);
			parser.setInput(getInputStream(u), null);
			int eventType = parser.getEventType();
			Line currentLine = null;
			boolean done = false;
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					lines = new ArrayList<Line>();
				}
				break;
				case XmlPullParser.START_TAG:
				{
					name = parser.getName();

					if (name.equalsIgnoreCase("LinesResp")){
						// do nothing
					}else if (name.equalsIgnoreCase("Lines")){
						// do nothing
					}else if (name.equalsIgnoreCase("Line")){
						currentLine = new Line();
					}
				}
				break;

				case XmlPullParser.TEXT:
				{
					if (currentLine != null){

						if (name.equalsIgnoreCase("DisplayName")){
							currentLine.setDisplayName(parser.getText());
						} else if (name.equalsIgnoreCase("EndStationCode")){
							currentLine.setEndStationCode(parser.getText());
						} else if (name.equalsIgnoreCase("InternalDestination1")){
							currentLine.setInternalDestination1(parser.getText());
						} else if (name.equalsIgnoreCase("InternalDestination2")){
							currentLine.setInternalDestination2(parser.getText());
						} else if (name.equalsIgnoreCase("LineCode")){
							currentLine.setLineCode(parser.getText());	
						} else if (name.equalsIgnoreCase("StartStationCode")){
							currentLine.setStartStationCode(parser.getText());	
						}      
					}
				}
				break;

				case XmlPullParser.END_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("Line") && currentLine != null){
						lines.add(currentLine);
					} else if (name.equalsIgnoreCase("Lines")){
						done = true;
					}
				}
				break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e(this.toString(), "error: " + e.toString());
		}
		return lines;
	}

	public ArrayList<Station> parseStations() {

		String url = new StringBuffer().append("http://api.wmata.com/Rail.svc/Stations")
				.append("?api_key=").append(getKey()).toString(); 

		ArrayList<Station> stations = null;
		XmlPullParser parser = Xml.newPullParser();

		try {
			// auto-detect the encoding from the stream
			URL u = getURL(url);
			parser.setInput(getInputStream(u), null);
			int eventType = parser.getEventType();
			Station currentStation = null;
			boolean done = false;
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					stations = new ArrayList<Station>();
				}
				break;

				case XmlPullParser.START_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("StationsResp")){
						// do nothing
					}else if (name.equalsIgnoreCase("Stations")){
						// do nothing
					}else if (name.equalsIgnoreCase("Station")){
						currentStation = new Station();
					}
				}
				break;

				case XmlPullParser.TEXT:
				{
					if (currentStation != null){
						if (name.equalsIgnoreCase("Code")){
							currentStation.setCode(parser.getText());
						} else if (name.equalsIgnoreCase("Lat")){
							currentStation.setLat(Float.parseFloat(parser.getText()));
						} else if (name.equalsIgnoreCase("LineCode1")){
							currentStation.setLineCode1(parser.getText());
						} else if (name.equalsIgnoreCase("LineCode2")){
							currentStation.setLineCode2(parser.getText());
						} else if (name.equalsIgnoreCase("LineCode3")){
							currentStation.setLineCode3(parser.getText());	
						} else if (name.equalsIgnoreCase("LineCode4")){
							currentStation.setLineCode4(parser.getText());	
						} else if (name.equalsIgnoreCase("Lon")){
							currentStation.setLon(Float.parseFloat(parser.getText()));	
						} else if (name.equalsIgnoreCase("Name")){
							currentStation.setName(parser.getText());	
						} else if (name.equalsIgnoreCase("StationTogether1")){
							currentStation.setStationTogether1(parser.getText());	
						} else if (name.equalsIgnoreCase("StationTogether2")){
							currentStation.setStationTogether2(parser.getText());	
						}            
					}
				}
				break;

				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("Station") && currentStation != null){
						stations.add(currentStation);
					} else if (name.equalsIgnoreCase("Stations")){
						done = true;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return stations;
	}

	public ArrayList<Station> parseStationsByLineCode(String linecode) {

		String url = new StringBuffer().append("http://api.wmata.com/Rail.svc/Stations")
				.append("?api_key=").append(getKey())
				.append("&LineCode=").append(linecode).toString();
		
		ArrayList<Station> stations = null;
		XmlPullParser parser = Xml.newPullParser();

		try {
			// auto-detect the encoding from the stream
			URL u = getURL(url);
			parser.setInput(getInputStream(u), null);
			int eventType = parser.getEventType();
			Station currentStation = null;
			boolean done = false;
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					stations = new ArrayList<Station>();
				}
				break;

				case XmlPullParser.START_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("StationsResp")){
						// do nothing
					}else if (name.equalsIgnoreCase("Stations")){
						// do nothing
					}else if (name.equalsIgnoreCase("Station")){
						currentStation = new Station();
					}
				}
				break;

				case XmlPullParser.TEXT:
				{

					if (currentStation != null){
						if (name.equalsIgnoreCase("Code")){
							currentStation.setCode(parser.getText());
						} else if (name.equalsIgnoreCase("Lat")){
							currentStation.setLat(Float.parseFloat(parser.getText()));
						} else if (name.equalsIgnoreCase("LineCode1")){
							currentStation.setLineCode1(parser.getText());
						} else if (name.equalsIgnoreCase("LineCode2")){
							currentStation.setLineCode2(parser.getText());
						} else if (name.equalsIgnoreCase("LineCode3")){
							currentStation.setLineCode3(parser.getText());	
						} else if (name.equalsIgnoreCase("LineCode4")){
							currentStation.setLineCode4(parser.getText());	
						} else if (name.equalsIgnoreCase("Lon")){
							currentStation.setLon(Float.parseFloat(parser.getText()));	
						} else if (name.equalsIgnoreCase("Name")){
							currentStation.setName(parser.getText());	
						} else if (name.equalsIgnoreCase("StationTogether1")){
							currentStation.setStationTogether1(parser.getText());	
						} else if (name.equalsIgnoreCase("StationTogether2")){
							currentStation.setStationTogether2(parser.getText());	
						}            
					}
				}
				break;

				case XmlPullParser.END_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("Station") && currentStation != null){
						stations.add(currentStation);
					} else if (name.equalsIgnoreCase("Stations")){
						done = true;
					}
				}
				break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return stations;
	}

	public List<RailStationPrediction> parseStationPrediction() {

		String url = new StringBuffer().append("http://api.wmata.com/StationPrediction.svc/GetPrediction/All")
				.append("?api_key=").append(getKey()).toString(); 

		List<RailStationPrediction> predictions = null;
		XmlPullParser parser = Xml.newPullParser();
		try {
			// auto-detect the encoding from the stream
			parser.setInput(getInputStream(getURL(url)), null);
			int eventType = parser.getEventType();
			RailStationPrediction currentStationPrediction = null;
			boolean done = false;
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){    

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					predictions = new ArrayList<RailStationPrediction>();
				}
				break;

				case XmlPullParser.START_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("AIMPredictionResp")){
						// do nothing
					}else if (name.equalsIgnoreCase("Trains")){
						// do nothing
					}else if (name.equalsIgnoreCase("AIMPredictionTrainInfo")){
						currentStationPrediction = new RailStationPrediction();   	
					}
				}
				break;

				case XmlPullParser.TEXT:
				{
					if (currentStationPrediction != null){

						if (name.equalsIgnoreCase("Car")){
							currentStationPrediction.setCar(parser.getText());
						} else if (name.equalsIgnoreCase("Destination")){
							currentStationPrediction.setDestination(parser.getText());
						} else if (name.equalsIgnoreCase("DestinationCode")){
							currentStationPrediction.setDestinationCode(parser.getText());
						} else if (name.equalsIgnoreCase("DestinationName")){
							currentStationPrediction.setDestinationName(parser.getText());
						} else if (name.equalsIgnoreCase("Group")){
							currentStationPrediction.setGroup(parser.getText());	
						} else if (name.equalsIgnoreCase("Line")){
							currentStationPrediction.setLine(parser.getText());	
						} else if (name.equalsIgnoreCase("LocationCode")){
							currentStationPrediction.setLocationCode(parser.getText());	
						} else if (name.equalsIgnoreCase("LocationName")){
							currentStationPrediction.setLocationName(parser.getText());	
						} else if (name.equalsIgnoreCase("Min")){
							currentStationPrediction.setMin(parser.getText());	
						}        
					}
				}
				break;

				case XmlPullParser.END_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("Trains") && currentStationPrediction != null){
						predictions.add(currentStationPrediction);
					} else if (name.equalsIgnoreCase("AIMPredictionTrainInfo")){
						done = true;
					}
				}
				break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return predictions;
	}

	public List<RailStationPrediction> parseStationPredictionByStation(ArrayList<String> stationcode) {

		String param = "";

		if (stationcode.size()== 1){
			param = stationcode.get(0);	
		}else if (stationcode.size() > 1){
			for (int i = 0; i < stationcode.size(); i++){
				param += stationcode.get(i)+",";
			}	
			param = param.substring(0, param.length()-1);
		}

		String url = new StringBuffer().append("http://api.wmata.com/StationPrediction.svc/GetPrediction/")
			.append(param).append("?api_key=").append(getKey()).toString(); 

		List<RailStationPrediction> predictions = null;
		XmlPullParser parser = Xml.newPullParser();
		try {
			// auto-detect the encoding from the stream
			parser.setInput(getInputStream(getURL(url)), null);
			int eventType = parser.getEventType();
			RailStationPrediction currentStationPrediction = null;
			boolean done = false;
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					predictions = new ArrayList<RailStationPrediction>();
				}
				break;

				case XmlPullParser.START_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("AIMPredictionResp")){
						// do nothing
					}else if (name.equalsIgnoreCase("Trains")){
						// do nothing
					}else if (name.equalsIgnoreCase("AIMPredictionTrainInfo")){
						currentStationPrediction = new RailStationPrediction();
					}                                        
				}
				break;

				case XmlPullParser.TEXT:
				{
					if (currentStationPrediction != null){

						if (name.equalsIgnoreCase("Car")){
							currentStationPrediction.setCar(parser.getText());
						} else if (name.equalsIgnoreCase("Destination")){
							currentStationPrediction.setDestination(parser.getText());
						} else if (name.equalsIgnoreCase("DestinationCode")){
							currentStationPrediction.setDestinationCode(parser.getText());
						} else if (name.equalsIgnoreCase("DestinationName")){
							currentStationPrediction.setDestinationName(parser.getText());
						} else if (name.equalsIgnoreCase("Group")){
							currentStationPrediction.setGroup(parser.getText());	
						} else if (name.equalsIgnoreCase("Line")){
							currentStationPrediction.setLine(parser.getText());	
						} else if (name.equalsIgnoreCase("LocationCode")){
							currentStationPrediction.setLocationCode(parser.getText());	
						} else if (name.equalsIgnoreCase("LocationName")){
							currentStationPrediction.setLocationName(parser.getText());	
						} else if (name.equalsIgnoreCase("Min")){
							currentStationPrediction.setMin(parser.getText());	
						}        
					}
				}
				break;     

				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("AIMPredictionTrainInfo") && currentStationPrediction != null){
						// WMATA throws bad entries sometimes. 
						if ((currentStationPrediction.getLine()!= null) 
								&& (!currentStationPrediction.getLine().equals("")) 
								&& (currentStationPrediction.getMin()!= null)
								&& (!currentStationPrediction.getDestinationName().equalsIgnoreCase("Train"))
								&& (!currentStationPrediction.getDestinationName().equalsIgnoreCase("No Passenger")))
						{	
							predictions.add(currentStationPrediction);
						}                        	
					} else if (name.equalsIgnoreCase("Trains")){
						done = true;
					}
					break;

				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			throw new RuntimeException(e);
		}
		return predictions;
	}

	public List<Station> parseHardCodedStationsByLine(Activity a, String lineCode){

		ArrayList<Station> stations = new ArrayList<Station>();
		XmlResourceParser parser = null;

		try {
			Resources res = a.getResources();
			if (lineCode.equalsIgnoreCase("GR")){
				parser = res.getXml(R.xml.stations_green);
			} else if (lineCode.equalsIgnoreCase("YL")){
				parser = res.getXml(R.xml.stations_yellow);
			} else if (lineCode.equalsIgnoreCase("BL")){
				parser = res.getXml(R.xml.stations_blue);
			} else if (lineCode.equalsIgnoreCase("RD")){
				parser = res.getXml(R.xml.stations_red);
			} else if (lineCode.equalsIgnoreCase("OR")){
				parser = res.getXml(R.xml.stations_orange);
			} else {
				parser = res.getXml(R.xml.all_stations);
			}

			parser.next();
		} catch (Exception e) {
			Log.e(this.toString(), "error: " + e.toString());
			throw new RuntimeException(e);
		}

		try {

			int eventType = parser.getEventType();
			Station currentStation = null;
			boolean done = false;
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					stations = new ArrayList<Station>();
				}
				break;

				case XmlPullParser.START_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("StationsResp")){
						// do nothing
					}else if (name.equalsIgnoreCase("Stations")){
						// do nothing
					}else if (name.equalsIgnoreCase("Station")){
						currentStation = new Station();
					}
				}
				break;

				case XmlPullParser.TEXT:
				{
					if (currentStation != null){
						if (name.equalsIgnoreCase("Code")){
							currentStation.setCode(parser.getText());
						} else if (name.equalsIgnoreCase("Lat")){
							currentStation.setLat(Float.parseFloat(parser.getText()));
						} else if (name.equalsIgnoreCase("LineCode1")){
							currentStation.setLineCode1(parser.getText());
						} else if (name.equalsIgnoreCase("LineCode2")){
							currentStation.setLineCode2(parser.getText());
						} else if (name.equalsIgnoreCase("LineCode3")){
							currentStation.setLineCode3(parser.getText());	
						} else if (name.equalsIgnoreCase("LineCode4")){
							currentStation.setLineCode4(parser.getText());	
						} else if (name.equalsIgnoreCase("Lon")){
							currentStation.setLon(Float.parseFloat(parser.getText()));	
						} else if (name.equalsIgnoreCase("Name")){
							currentStation.setName(parser.getText());	
						} else if (name.equalsIgnoreCase("StationTogether1")){
							currentStation.setStationTogether1(parser.getText());	
						} else if (name.equalsIgnoreCase("StationTogether2")){
							currentStation.setStationTogether2(parser.getText());	
						}            
					}
				}
				break;

				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("Station") && currentStation != null){
						if (lineCode.equalsIgnoreCase(currentStation.getLineCode1()) || 
								lineCode.equalsIgnoreCase(currentStation.getLineCode2()) || 
								lineCode.equalsIgnoreCase(currentStation.getLineCode3()) || 
								lineCode.equalsIgnoreCase(currentStation.getLineCode4())){

							stations.add(currentStation);
						}
					} else if (name.equalsIgnoreCase("Stations")){
						done = true;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e("Exception ", e.toString());
			throw new RuntimeException(e);
		}
		return stations;
	}

	public List<Line> parseHardCodedLines(Activity a){

		ArrayList<Line> lines = new ArrayList<Line>();
		XmlResourceParser parser = null;

		try {
			Resources res = a.getResources();
			parser = res.getXml(R.xml.all_lines);
			parser.next();
		} catch (Exception e) {
			Log.e(this.toString(), "error: " + e.toString());
			throw new RuntimeException(e);
		}

		try {

			int eventType = parser.getEventType();
			Line currentLine = null;
			boolean done = false;
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					lines = new ArrayList<Line>();
				}
				break;
				case XmlPullParser.START_TAG:
				{
					name = parser.getName();

					if (name.equalsIgnoreCase("LinesResp")){
						// do nothing
					}else if (name.equalsIgnoreCase("Lines")){
						// do nothing
					}else if (name.equalsIgnoreCase("Line")){
						currentLine = new Line();
					}
				}
				break;

				case XmlPullParser.TEXT:
				{
					if (currentLine != null){

						if (name.equalsIgnoreCase("DisplayName")){
							currentLine.setDisplayName(parser.getText());
						} else if (name.equalsIgnoreCase("EndStationCode")){
							currentLine.setEndStationCode(parser.getText());
						} else if (name.equalsIgnoreCase("InternalDestination1")){
							currentLine.setInternalDestination1(parser.getText());
						} else if (name.equalsIgnoreCase("InternalDestination2")){
							currentLine.setInternalDestination2(parser.getText());
						} else if (name.equalsIgnoreCase("LineCode")){
							currentLine.setLineCode(parser.getText());	
						} else if (name.equalsIgnoreCase("StartStationCode")){
							currentLine.setStartStationCode(parser.getText());	
						}      
					}
				}
				break;

				case XmlPullParser.END_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("Line") && currentLine != null){
						lines.add(currentLine);
					} else if (name.equalsIgnoreCase("Lines")){
						done = true;
					}
				}
				break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e(this.toString(), "error: " + e.toString());
		}
		return lines;
	}

	public ArrayList<String> getStationCodesByStation(String stationCode, Activity a){

		// some stations have more than one station code. this accepts a 
		// station code and returns all codes associated with that station.
	
		ArrayList<String> Codes = new ArrayList<String>();
		Codes.add(stationCode);
		boolean interested = false;
		
		XmlResourceParser parser = null;

		try {
			Resources res = a.getResources();
			parser = res.getXml(R.xml.all_stations);
			parser.next();
			
		} catch (Exception e) {
			Log.e(TAG, "error: " + e.toString());
			throw new RuntimeException(e);
		}

		try {

			int eventType = parser.getEventType();			
			boolean done = false;
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					// do nothing!
				}
				break;

				case XmlPullParser.START_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("StationsResp")){
						// do nothing
					}else if (name.equalsIgnoreCase("Stations")){
						// do nothing
					}else if (name.equalsIgnoreCase("Station")){
						// do nothing
					}
				}
				break;

				case XmlPullParser.TEXT:
				{

					if (name.equalsIgnoreCase("Code")){
						if (parser.getText().equalsIgnoreCase(stationCode)){
							// we hit the correct station!
							interested = true;
						}else{
							//back off!
							interested = false;
						}
					} else if ((name.equalsIgnoreCase("StationTogether1")) && interested){
						String s = parser.getText();	
						
						if((null != s) && (s.length() != 0)) {
							Codes.add(s);
						}
					} else if ((name.equalsIgnoreCase("StationTogether2")) && interested){
						String s = parser.getText();	
						if((null != s) && (s.length() != 0)) {
							Codes.add(s);
							// only 2 "station togethers" allowed
							done = true;
						}
					}            
				}
				break;

				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("Stations")){
						done = true;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e("Exception ", e.toString());
			throw new RuntimeException(e);
		}
		return Codes;
	}
	
	public List<RailIncident> parseRailIncident() {

		String url = new StringBuffer().append("http://api.wmata.com//Incidents.svc/Incidents")
			.append("?api_key=").append(getKey()).toString(); 

		List<RailIncident> incidents = null;
		XmlPullParser parser = Xml.newPullParser();
		try {
			// auto-detect the encoding from the stream
			parser.setInput(getInputStream(getURL(url)), null);
			int eventType = parser.getEventType();
			RailIncident railIncident = null;
			boolean done = false;
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){    

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					incidents = new ArrayList<RailIncident>();
				}
				break;

				case XmlPullParser.START_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("IncidentsResp")){
						// do nothing
					}else if (name.equalsIgnoreCase("Incidents")){
						// do nothing
					}else if (name.equalsIgnoreCase("Incident")){
						railIncident = new RailIncident();   	
					}
				}
				break;

				case XmlPullParser.TEXT:
				{
					if (railIncident != null){

						if (name.equalsIgnoreCase("DateUpdated")){
							railIncident.setDateUpdated(parser.getText());
						} else if (name.equalsIgnoreCase("DelaySeverity")){
							railIncident.setDelaySeverity(parser.getText());
						} else if (name.equalsIgnoreCase("Description")){
							railIncident.setDescription(parser.getText());
						} else if (name.equalsIgnoreCase("EmergencyText")){
							railIncident.setEmergencyText(parser.getText());
						} else if (name.equalsIgnoreCase("EndLocationFullName")){
							railIncident.setEndLocationFullName(parser.getText());	
						} else if (name.equalsIgnoreCase("IncidentID")){
							int i = -1;
							try{
								i = Integer.parseInt(parser.getText());
							}catch(Exception e){
								Log.e(TAG, e.toString());
							}
							railIncident.setIncidentID(i);	
						} else if (name.equalsIgnoreCase("IncidentType")){
							railIncident.setIncidentType(parser.getText());	
						} else if (name.equalsIgnoreCase("LinesAffected")){
							String[] array = parser.getText().split(",");
							railIncident.setLinesAffected(array);	
						} else if (name.equalsIgnoreCase("PassengerDelay")){
							int i = -1;
							try{
								i = Integer.parseInt(parser.getText());
							}catch(Exception e){
								Log.e(TAG, e.toString());
							}	
							railIncident.setPassengerDelay(i);	
						}  else if (name.equalsIgnoreCase("StartLocationFullName")){
							railIncident.setStartLocationFullName(parser.getText());	
						}        
					}
				}
				break;

				case XmlPullParser.END_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("Incident") && railIncident != null){
						incidents.add(railIncident);
					}else if (name.equalsIgnoreCase("Incidents")){
						done = true;
					}else if (name.equalsIgnoreCase("IncidentsResp")){
						done = true;
					}
				}
				break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return incidents;
	}
	
	public ArrayList<Route> parseBusLines() {

		String url = new StringBuffer().append("http://api.wmata.com/Bus.svc/Routes")
			.append("?api_key=").append(getKey()).toString(); 

		ArrayList<Route> lines = null;
		XmlPullParser parser = Xml.newPullParser();

		try {

			URL u = getURL(url);
			parser.setInput(getInputStream(u), null);
			int eventType = parser.getEventType();
			Route currentLine = null;
			boolean done = false;
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					lines = new ArrayList<Route>();
				}
				break;
				case XmlPullParser.START_TAG:
				{
					name = parser.getName();

					if (name.equalsIgnoreCase("RoutesResp")){
						// do nothing
					}else if (name.equalsIgnoreCase("Routes")){
						// do nothing
					}else if (name.equalsIgnoreCase("Route")){
						currentLine = new Route();
					}
				}
				break;

				case XmlPullParser.TEXT:
				{
					if (currentLine != null){

						if (name.equalsIgnoreCase("Name")){
							currentLine.setName(parser.getText());
						} else if (name.equalsIgnoreCase("RouteID")){
							currentLine.setRouteID(parser.getText());
						}
					}
				}
				break;

				case XmlPullParser.END_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("Route") && currentLine != null){
						lines.add(currentLine);
					} else if (name.equalsIgnoreCase("Routes")){
						done = true;
					}
				}
				break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e(this.toString(), "error: " + e.toString());
		}
		return lines;
	}
	
	public ArrayList<BusStop> parseBusStops() {
	
		return parseBusStops(null, null, null);
	}

	public ArrayList<BusStop> parseBusStops(Double lat, Double lon, Integer radius) {
	
		StringBuffer url = new StringBuffer().append("http://api.wmata.com/Bus.svc/Stops")
											 .append("?api_key=").append(getKey());
		
		if ((lat != null) && (lon != null && radius != null)){	
			url.append("&lat=").append(lat)
			   .append("&lon=").append(lon)
			   .append("&radius=").append(radius);
		}

		ArrayList<BusStop> lines = null;
		XmlPullParser parser = Xml.newPullParser();

		try {

			URL u = getURL(url.toString());
			parser.setInput(getInputStream(u), null);
			int eventType = parser.getEventType();
			BusStop currentLine = null;
			boolean done = false;
			boolean inRoutes = false;
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT && !done){

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					lines = new ArrayList<BusStop>();
				}
				break;
				case XmlPullParser.START_TAG:
				{
					name = parser.getName();

					if (name.equalsIgnoreCase("StopsResp")){
						// do nothing
					}else if (name.equalsIgnoreCase("Stops")){
						// do nothing
					}else if (name.equalsIgnoreCase("Stop")){
						currentLine = new BusStop();
					}
				}
				break;

				case XmlPullParser.TEXT:
				{
					if (currentLine != null){

						if (name.equalsIgnoreCase("Lat")){
							currentLine.setLat(Float.parseFloat(parser.getText()));
						} else if (name.equalsIgnoreCase("Lon")){
							currentLine.setLon(Float.parseFloat(parser.getText()));
						} else if (name.equalsIgnoreCase("Name")){
							currentLine.setName(parser.getText());
						} else if (name.equalsIgnoreCase("Routes")){
							//inRoutes=true;
						} else if (name.equalsIgnoreCase("a:string")){
							ArrayList<String> ar = currentLine.getRoutes();
							ar.add(parser.getText());
							currentLine.setRoutes(ar);
						}
					}
				}
				break;

				case XmlPullParser.END_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("Routes")){
						//inRoutes=false;
					} else if (name.equalsIgnoreCase("Stop") && currentLine != null){
						lines.add(currentLine);
					} else if (name.equalsIgnoreCase("Stops")){
						done = true;
					}
				}
				break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e(this.toString(), "error: " + e.toString());
		}
		return lines;
	}	

	public ArrayList<StopScheduleArrival> parseBusSchedules(String routeID, boolean includingVariations) {
		
		String url = new StringBuffer().append("http://api.wmata.com/Bus.svc/RouteSchedule")
					.append("?api_key=").append(getKey())
					.append("&routeID=").append(routeID)
					.append("&includingVariations=").append(includingVariations).toString();
		
		ArrayList<StopScheduleArrival> lines = null;
		XmlPullParser parser = Xml.newPullParser();

		try {

			URL u = getURL(url);
			parser.setInput(getInputStream(u), null);
			int eventType = parser.getEventType();
			RouteScheduleInfo currentLine = null;
			BusScheduleStopTime bsst = null;
			ArrayList<BusScheduleStopTime> ar = null;
			boolean done = false;
			boolean inRoutes = false;
			String name = null;
			
			while (eventType != XmlPullParser.END_DOCUMENT && !done){

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					lines = new ArrayList<StopScheduleArrival>();
				}
				break;
				case XmlPullParser.START_TAG:
				{
					name = parser.getName();

					if (name.equalsIgnoreCase("RouteScheduleInfo")){
						currentLine = new RouteScheduleInfo();
					} else if (name.equalsIgnoreCase("Direction0")){
						currentLine.setDirectionNum("Direction0");
					} else if (name.equalsIgnoreCase("Direction1")){
						currentLine.setDirectionNum("Direction1");
					} 
				}
				break;

				case XmlPullParser.TEXT:
				{
					if (currentLine != null){
						
						if (name.equalsIgnoreCase("EndTime")){
							currentLine.setEndTime(parser.getText());
						} else if (name.equalsIgnoreCase("RouteID")){
							currentLine.setRouteID(parser.getText());
						} else if (name.equalsIgnoreCase("StartTime")){
							currentLine.setStartTime(parser.getText());
						} else if (name.equalsIgnoreCase("StopTimes")){
							inRoutes=true;
						} else if (name.equalsIgnoreCase("StopTime") && inRoutes){
							if (currentLine.getStopTimes() == null){
								currentLine.setStopTimes(new ArrayList<BusScheduleStopTime>());
							}
							bsst = new BusScheduleStopTime();
							ar = currentLine.getStopTimes();
						}else if (name.equalsIgnoreCase("StopID")){
							bsst.setStopID(parser.getText());
						}else if (name.equalsIgnoreCase("StopName")){
							bsst.setStopName(parser.getText());
						}else if (name.equalsIgnoreCase("StopSeq")){
							bsst.setStopSeq(parser.getText());
						}else if (name.equalsIgnoreCase("Time")){
							bsst.setTime(parser.getText());	
						}else if (name.equalsIgnoreCase("TripDirectionText")){
							currentLine.setTripDirectionText(parser.getText());
						}else if (name.equalsIgnoreCase("TripHeadsign")){
							currentLine.setTripHeadsign(parser.getText());
						}else if (name.equalsIgnoreCase("TripID")){
							currentLine.setTripID(parser.getText());
						}	
					}
				}
				break;

				case XmlPullParser.END_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("StopTime")){
						ar.add(bsst);
					} else if (name.equalsIgnoreCase("StopTimes")){
						inRoutes=false;
						currentLine.setStopTimes(ar);
						bsst = null;
						ar = null;
					}  else if (name.equalsIgnoreCase("Trip")){
						done = true;
					}
				}
				break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e(this.toString(), "error: " + e.toString());
		}
		return lines;
	}
	
	public ArrayList<BusPosition> parseBusPositions(String routeID, boolean includingVariations) {

		String url = new StringBuffer().append("http://api.wmata.com/Bus.svc/BusPositions")
					.append("?api_key=").append(getKey())
					.append("&routeID=").append(routeID)
					.append("&includingVariations=").append(includingVariations).toString();
		
		ArrayList<BusPosition> lines = null;
		XmlPullParser parser = Xml.newPullParser();

		try {

			URL u = getURL(url);
			parser.setInput(getInputStream(u), null);
			int eventType = parser.getEventType();
			BusPosition currentLine = null;
			boolean done = false;
			boolean inRoutes = false;
			String name = null;
	
			while (eventType != XmlPullParser.END_DOCUMENT && !done){

				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
				{
					lines = new ArrayList<BusPosition>();
				}
				break;
				case XmlPullParser.START_TAG:
				{
					name = parser.getName();

					if (name.equalsIgnoreCase("BusPositionsResp")){
						// do nothing
					} else if (name.equalsIgnoreCase("BusPositions")){
						// do nothing
					} else if (name.equalsIgnoreCase("BusPosition")){
						currentLine = new BusPosition();
					} 
				}
				break;

				case XmlPullParser.TEXT:
				{
					if (currentLine != null){
						
						if (name.equalsIgnoreCase("DateTime")){
							currentLine.setDateTime(parser.getText());
						} else if (name.equalsIgnoreCase("Deviation")){
							currentLine.setDeviation(Float.parseFloat(parser.getText()));
						} else if (name.equalsIgnoreCase("DirectionNum")){
							currentLine.setDirectionNum(Integer.parseInt(parser.getText()));
						} else if (name.equalsIgnoreCase("DirectionText")){
							currentLine.setDirectionText(parser.getText());
						} else if (name.equalsIgnoreCase("Lat")){
							currentLine.setLat(Float.parseFloat(parser.getText()));
						} else if (name.equalsIgnoreCase("Lon")){
							currentLine.setLon(Float.parseFloat(parser.getText()));
						} else if (name.equalsIgnoreCase("RouteID")){
							currentLine.setRouteID(parser.getText());
						} else if (name.equalsIgnoreCase("TripHeadsign")){
							currentLine.setTripHeadsign(parser.getText());
						} else if (name.equalsIgnoreCase("TripID")){
							currentLine.setTripID(parser.getText());
						} else if (name.equalsIgnoreCase("TripStartTime")){
							currentLine.setTripStartTime(parser.getText());
						} else if (name.equalsIgnoreCase("VehicleID")){
							currentLine.setVehicleID(parser.getText());
						}	
					}
				}
				break;

				case XmlPullParser.END_TAG:
				{
					name = parser.getName();
					if (name.equalsIgnoreCase("BusPosition")){
						lines.add(currentLine);
					} else if (name.equalsIgnoreCase("BusPositions")){
						done = true;
					}  else if (name.equalsIgnoreCase("BusPositionsResp")){
						//done.
					}
				}
				break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.e(this.toString(), "error: " + e.toString());
		}
		return lines;
	}
}
