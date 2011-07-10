package com.sombrerosoft.android.wmata;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.sombrerosoft.android.wmata.R;
import com.sombrerosoft.android.wmata.R.id;
import com.sombrerosoft.android.wmata.R.layout;
import com.sombrerosoft.android.wmata.R.menu;
import com.sombrerosoft.android.wmata.R.string;
import com.sombrerosoft.android.wmata.dal.XmlPullFeedParser;
import com.sombrerosoft.wmata.beans.bus.BusStop;
import com.sombrerosoft.wmata.beans.bus.Route;
import com.sombrerosoft.wmata.beans.metro.Station;

public class BusActivity extends Activity {

	private ListView lv;
	private TextView lastupdated;
	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<BusStop> m_orders = null;
	private OrderAdapter m_adapter;
	private String lineCode = null;
	private Location mLocation;
	private LocationManager mLocationManager;
	private String SYSTEM_OF_MEASUREMENT = "METRIC";
	private boolean network_enabled = false;
    private String REFRESH_DELAY_MS = "30000";
    private int ms = 30000;
    private boolean firstRun = true;
	boolean isReady = true;

	private Handler mHandler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	    	super.handleMessage(msg);
	    }
	};

	private String FORMAT;
	private String TAG = "BusActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
	}

	
	private Runnable mUpdateTimeTask = new Runnable() {
		
		@Override
		public void run() {

			getOrders();
			
			mHandler.post(mUpdateResults);
			mHandler.handleMessage(null);
			mHandler.postDelayed(mUpdateTimeTask, ms);
		}
	};
	
    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };
    
    private void updateResultsInUi() {
    	
    	if (m_ProgressDialog != null){
    		m_ProgressDialog.dismiss();
    	}
    	
        // Back in the UI thread -- update our UI elements based on the data in mResults
		GregorianCalendar now = new GregorianCalendar();
		Date d =  now.getTime();
		DateFormat df1 = DateFormat.getDateTimeInstance();
		String s1 = df1.format(d);

		String location = "";
		if (mLocation != null){
			location = mLocation.getLatitude() + " lat, " + mLocation.getLongitude() + " lon";
			if (firstRun){
				location = "Using last known location (update pending):\n";
				firstRun = false;
			}else{
				location = "Detected location:\n";
			}
			location += mLocation.getLatitude() + " lat, " + mLocation.getLongitude() + " lon";
		}
		
		lastupdated.setText("Data refreshed every " + ms/1000 + " seconds.\nCurrent as of " + s1 + "\n" + location);
		
		if(m_orders != null && m_orders.size() > 0){
			m_adapter.notifyDataSetChanged();
			m_adapter.clear();
			for(int i=0;i<m_orders.size();i++){
				m_adapter.add(m_orders.get(i));
			}               
		}

		m_adapter.notifyDataSetChanged();
    }

	@Override    
	public void onResume() {	
		super.onResume();

		setContentView(R.layout.my_location);
		lv = (ListView)findViewById(R.id.gridview);
		lastupdated = (TextView)findViewById(R.id.lastupdated);
		
		m_ProgressDialog = new ProgressDialog(this);
		m_ProgressDialog.setTitle("Please wait...");
		m_ProgressDialog.setMessage("Retrieving data ...");
		m_ProgressDialog.setCancelable(true);
		m_ProgressDialog.show();
		
        Thread thread = new Thread(mUpdateTimeTask);
        thread.start();
        
		//mHandler.post(mUpdateTimeTask);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		FORMAT = prefs.getString("RESULT_FORMAT", "TABLE"); 
    	REFRESH_DELAY_MS = prefs.getString("REFRESH_DELAY_MS", "30000");
		SYSTEM_OF_MEASUREMENT = prefs.getString("MEASUREMENT_TYPE", "METRIC");
		
		try{
			ms = Integer.parseInt(REFRESH_DELAY_MS);
		}catch(Exception ex){
			Log.e(TAG, "cannot parse REFRESH_DELAY_MS" + ex.toString());
		}
			
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		try{
			network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			//Log.d(TAG, "NETWORK_PROVIDER enabled");
		}catch(Exception ex){
			//Log.e(TAG, "NETWORK_PROVIDER not enabled: " + ex.toString());
		}   

		if (network_enabled){
			mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, ms, (float) 5, myLocL); 	
		}else{
			
			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setMessage("Cannot determine location: NETWORK_PROVIDER.");
			alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					//Toast.makeText(getApplicationContext(), "OK button clicked", Toast.LENGTH_LONG).show();
				}
			});
			alertbox.show();
			return;
		}

		//this.lv = (ListView)this.findViewById();  
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent newIntent;

				newIntent = new Intent(BusActivity.this, BusTimeOfArrivalActivity.class);
			
				Station s = (Station) lv.getItemAtPosition(arg2);

				newIntent.putExtra("lineCode", lineCode);
				newIntent.putExtra("stationCode", s.getCode());
				newIntent.putExtra("stationName", s.getName());

				m_orders = null;

				startActivity(newIntent);

			}
		});

		m_orders = new ArrayList<BusStop>();
		this.m_adapter = new OrderAdapter(this, R.layout.closest_row, m_orders);
		lv.setAdapter(this.m_adapter);  

	}

	@SuppressWarnings("unchecked")
	private void getOrders(){
/*
		try{        	  
			// dynamic:
			// 1) parse all lines, get line codes
			// 2) parse all line codes, get each closest station
			// 3) add to arraylist and return

			XmlPullFeedParser xmlpull = XmlPullFeedParser.getXmlPullFeedParser();
			ArrayList<Route> lines = (ArrayList<Route>)  xmlpull.parseBusLines();
			
			ArrayList<BusStop> s =  new ArrayList<BusStop>();


			float shortest_distance = Float.MAX_VALUE;
			BusStop closest_station = null;

			for (int i = 0; i < lines.size();i++){

				float[] results = new float[3];

				ArrayList<Route> stations = (ArrayList<Route>) xmlpull.parseBusLines();

				for (int j = 0; j < stations.size(); j++){

					Location.distanceBetween(mLocation.getLatitude(), mLocation.getLongitude(), stations.get(j).getLat(), stations.get(j).getLon(), results);
					
					stations.get(j).setDistanceFromMe(results[0]);

					Location endPoint = new Location("");
					endPoint.setLatitude(stations.get(j).getLat());
					endPoint.setLongitude(stations.get(j).getLon());

					float bearing = mLocation.bearingTo(endPoint); 

					if (bearing<0) {
						bearing = 360+bearing; 
					}

					stations.get(j).setBearing(bearing);

					if ((float)results[0] < shortest_distance){
						shortest_distance = (float)results[0];
						closest_station = stations.get(j);
					}
				}
				shortest_distance = Float.MAX_VALUE;

				boolean f = false;
				for (int k = 0; k < s.size(); k++){
					if (s.get(k).getName().equalsIgnoreCase(closest_station.getName())){
						f = true;
						break;
					}
				}
				if (!f){
					s.add(closest_station);
				}

				//Log.d(TAG ,"add: " + closest_station);
			}

			Collections.sort(s, new Comparator(){		 
				public int compare(Object o1, Object o2) {
					Station p1 = (Station) o1;
					Station p2 = (Station) o2;
					return (Float.compare(p1.getDistanceFromMe(), p2.getDistanceFromMe()));
				}
			}
			);

			m_orders = s;
			
		} catch (Exception e) {
			Log.e(TAG, "BACKGROUND_PROC " + e.getLocalizedMessage());
		}
		
		*/
		
	}

	private class OrderAdapter extends ArrayAdapter<BusStop> {

		private ArrayList<BusStop> items;

		public OrderAdapter(Context context, int textViewResourceId, ArrayList<BusStop> mOrders) {
			super(context, textViewResourceId, mOrders);
			this.items = mOrders;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.closest_row, null);
			}
			
			BusStop o = items.get(position);

			if (o != null) {

				TextView tt = (TextView) v.findViewById(R.id.toptext);
				String text = "";

				float[] results = new float[3];            	
				Location.distanceBetween(mLocation.getLatitude(), mLocation.getLongitude(), o.getLat(), o.getLon(), results);

				Location endPoint = new Location("");
				endPoint.setLatitude(o.getLat());
				endPoint.setLongitude(o.getLon());

				float bearing = mLocation.bearingTo(endPoint);

				o.setBearing(bearing);
				o.setDistanceFromMe(results[0]);

				if (tt != null) {

					if (SYSTEM_OF_MEASUREMENT.equalsIgnoreCase("METRIC")){

						double meters = o.getDistanceFromMe();
						DecimalFormat twoDForm = new DecimalFormat("#.##");

						if (meters > 1000){ 			
							double kilometers = meters/1000;    		
							kilometers = Double.valueOf(twoDForm.format(kilometers));
							text += "&nbsp;" + o.getName() + "<br>&nbsp;" + kilometers + " km"; 
						}else{
							text += "  " + o.getName() + "<br>&nbsp;" + (int) meters + " m"; 
						}

					}else{
						double feet = (float) (o.getDistanceFromMe() * 3.2808399);

						DecimalFormat twoDForm = new DecimalFormat("#.##");
						double miles = (feet/5280);

						text += "&nbsp;" + o.getName() + "<br>&nbsp;";

						if (miles > 1){

							miles = Double.valueOf(twoDForm.format(miles));
							text += miles + " mi.";
						}else{
							feet = Double.valueOf(twoDForm.format(feet));
							text += (int) feet + " ft.";
						}

					}

					tt.setText(Html.fromHtml(text));	
				}
			}
			return v;
		}
	}

	private final LocationListener myLocL = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {	
			mLocation = location;
		}
		@Override
		public void onProviderDisabled(String provider) {

		}
		@Override
		public void onProviderEnabled(String provider) {}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}	
	};

	/*
	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(mUpdateTimeTask);
		mLocationManager.removeUpdates(myLocL);

	}	
	*/
	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mUpdateTimeTask);
		mLocationManager.removeUpdates(myLocL);
	}	
	
    @Override    
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.app_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.string.GPS_closest:
        {
        	Intent settingsActivity = new Intent(getBaseContext(), LocationActivity.class);
        	startActivity(settingsActivity);
        }
        return true;
        case R.string.about:
        {
        	String app_ver = "";
        	
        	try{
        		app_ver = " v" + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        	}
        	catch (NameNotFoundException e){
        	    Log.v(TAG, e.getMessage());
        	}
        	
        	String about = "wdc metro locator app" + app_ver + "\nby brian reed\nsupport@amsoftgroup.com";
        	
            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setMessage(about);
            alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                //    Toast.makeText(getApplicationContext(), "OK button clicked", Toast.LENGTH_LONG).show();
                }
            });
            alertbox.show();
        }
        return true;
        case R.string.status:
        {
        	Intent settingsActivity = new Intent(getBaseContext(), WebStatusActivity.class);
        	startActivity(settingsActivity);
        }
        return true;
        case R.string.settings:
        {
        	Intent settingsActivity = new Intent(getBaseContext(), WMATAPreferenceActivity.class);
        	startActivity(settingsActivity);
        }
        return true;
        default:
            return super.onOptionsItemSelected(item);
        }


    } 
}
