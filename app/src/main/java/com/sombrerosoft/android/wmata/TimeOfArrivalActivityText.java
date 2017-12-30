package com.sombrerosoft.android.wmata;

import java.text.DateFormat;
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
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.sombrerosoft.android.wmata.R;
import com.sombrerosoft.android.wmata.R.id;
import com.sombrerosoft.android.wmata.R.layout;
import com.sombrerosoft.android.wmata.R.menu;
import com.sombrerosoft.android.wmata.R.string;
import com.sombrerosoft.android.wmata.dal.XmlPullFeedParser;
import com.sombrerosoft.android.wmata.helper.AppHelper;
import com.sombrerosoft.wmata.beans.metro.RailStationPrediction;

public class TimeOfArrivalActivityText  extends Activity{

	private String stationCode;
	private String lineCode;
    private ProgressDialog m_ProgressDialog = null;
    private ArrayList<RailStationPrediction> m_orders = null;
    private TextView tv_station;
    private TextView tv;
    private TextView lastupdated;
    private String REFRESH_DELAY_MS = "30000";
    private int ms = 30000;
	private Handler mHandler = new Handler();
	private String TAG = "TimeOfArrivalActivityText";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);    
		
     	ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
     	if (!AppHelper.isConnected(connec))
     	{
     		this.setContentView(R.layout.error);
     		
        	TextView destination = (TextView) findViewById(R.id.error);
        	destination.setText("No network connection!\nPlease try again later.");
        	
     	}else{
     		this.setContentView(R.layout.time_of_arrival_activity);

     		stationCode = null;
     		
     		tv = (TextView)findViewById(R.id.status);
     		tv_station = (TextView)findViewById(R.id.toa_station);
     		lastupdated = (TextView)findViewById(R.id.lastupdated);

     		try {
     			Bundle bundle = this.getIntent().getExtras();
     			stationCode = bundle.getString("stationCode");
     		} catch (Exception e) {
     			Log.e(TAG, "error: " + e.toString());
     		}
     		try {
     			Bundle bundle = this.getIntent().getExtras();
     			lineCode = bundle.getString("lineCode");
     		} catch (Exception e) {
     			Log.e(TAG, "error: " + e.toString());
     		}

     		Thread thread =  new Thread(mUpdateTimeTask);
     		thread.start();    
     	}
	}
	
	private Runnable mUpdateTimeTask = new Runnable() {
		
		@Override
		public void run() {

			update();
			
			mHandler.post(mUpdateResults);
			//mHandler.handleMessage(null);
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

    	// Back in the UI thread -- update our UI elements based on the data in mResults
    	if (m_ProgressDialog != null){
    		m_ProgressDialog.dismiss();
    	}	

    	if (m_orders != null){

    		String station = m_orders.get(0).getLocationName();
    		tv_station.setText(station);
    		String c = "";
    		String status = "";
    		if (m_orders.get(0).getLine() == null){
    			c = "No Passengers";
    		}else{
    			c = m_orders.get(0).getLine();
    		}

    		String line = "<b>" + AppHelper.resolveLineCode(c) + " to: </b><br/>";
    		status += line;

    		for (int i =0; i < m_orders.size(); i++){
    			if (!m_orders.get(i).getLine().equalsIgnoreCase(c)){
    				c = m_orders.get(i).getLine();
    				line = AppHelper.resolveLineCode(c);
    				status += "<b>" + line + " to: </b><br/>";
    			}
    			status += "	" + m_orders.get(i).getDestinationName() + " " +  m_orders.get(i).getMin() + " (" + m_orders.get(i).getCar() + " cars)<br/>"; 	
    		}	
    		tv.setText(Html.fromHtml(status));

    	}else if (m_orders == null){
    		tv.setText("No data returned from WMATA.com.");
    	}

    	GregorianCalendar now = new GregorianCalendar();
    	Date d =  now.getTime();
		DateFormat df1 = DateFormat.getDateTimeInstance();
		String s1 = df1.format(d);

		lastupdated.setText("Data refreshed every " + Integer.parseInt(REFRESH_DELAY_MS)/1000 + " seconds.\nCurrent as of " + s1);
    }

	@Override
	public void onResume() {
		super.onResume();
	
		ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

		if (AppHelper.isConnected(connec)){

			m_ProgressDialog = new ProgressDialog(this);
			m_ProgressDialog.setTitle("Please wait...");
			m_ProgressDialog.setMessage("Retrieving data ...");
			m_ProgressDialog.setCancelable(true);
			m_ProgressDialog.show();

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			REFRESH_DELAY_MS = prefs.getString("REFRESH_DELAY_MS", "30000");

			Thread thread = new Thread(mUpdateTimeTask);
			thread.start();

			try{
				ms = Integer.parseInt(REFRESH_DELAY_MS);
			}catch(Exception ex){
				Log.e(TAG, "cannot parse REFRESH_DELAY_MS" + ex.toString());
			}
		}
	}
	
    private void update(){

    	ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);	
    	if (AppHelper.isConnected(connec))
    	{
    		try{
    			XmlPullFeedParser xmlpull = XmlPullFeedParser.getXmlPullFeedParser(this);
    			//get Lines by StationCode... some stations have more than one code!
    			// TODO: is there a better way to do this than re-parse the XML every 15-60 second refresh?
    			ArrayList<String> codes = xmlpull.getStationCodesByStation(stationCode, this);

    			m_orders = (ArrayList<RailStationPrediction>) xmlpull.parseStationPredictionByStation(codes);

    			//sort m_orders based on line code
    			Collections.sort(m_orders, new CustomComparator());
    		} catch (Exception e) {
    			Log.e(TAG, e.getMessage());
    		}
    	}
    	
    }

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mUpdateTimeTask);
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
        	
        	String about = "wdc metro locator app" + app_ver + "\nsupport@amsoftgroup.com";
        		
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
        case R.string.voice:
        {
        	Intent settingsActivity = new Intent(getBaseContext(), VoiceStartActivity.class);
        	startActivity(settingsActivity);
        }
        return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}   

	public class CustomComparator implements Comparator<RailStationPrediction> {
		@Override
		public int compare(RailStationPrediction o1, RailStationPrediction o2) {
			if (o1.getLine() == null){
				o1.setLine("No Passengers");
			} 
			if (o2.getLine() == null){
				o2.setLine("No Passengers");
			}
			//Log.d("COMPARE", o1.getLine() + " + " + o2.getLine());
			return o1.getLine().compareTo(o2.getLine());
		}
	} 
	
	
}