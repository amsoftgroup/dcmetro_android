package com.sombrerosoft.android.wmata;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import com.sombrerosoft.android.wmata.R;
import com.sombrerosoft.android.wmata.R.drawable;
import com.sombrerosoft.android.wmata.R.id;
import com.sombrerosoft.android.wmata.R.layout;
import com.sombrerosoft.android.wmata.R.menu;
import com.sombrerosoft.android.wmata.R.string;
import com.sombrerosoft.android.wmata.dal.XmlPullFeedParser;
import com.sombrerosoft.android.wmata.helper.AppHelper;
import com.sombrerosoft.wmata.beans.metro.RailStationPrediction;
import com.sombrerosoft.wmata.beans.metro.Station;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;

public class BusTimeOfArrivalActivity  extends Activity{
	
	private String stationCode;
	private String stationName;
    private ProgressDialog m_ProgressDialog = null;
    private ArrayList<RailStationPrediction> m_orders = null;
    private TextView tv_station;
    private TextView lastupdated;
    private ListView grid;
    private String REFRESH_DELAY_MS_STRING = "30000";
    private int REFRESH_DELAY_MS_INT = 30000;
    private ArrivalAdapter m_adapter;
	private Handler mHandler = new Handler();

    private String TAG = "TimeOfArrivalActivity";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);         

    	if (m_ProgressDialog != null){
    		m_ProgressDialog.dismiss();
    	}
    	
     	ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	
        if (!AppHelper.isConnected(connec)){
        	this.setContentView(R.layout.error);
        	TextView destination = (TextView) findViewById(R.id.error);
        	destination.setText("No network connection!\nPlease try again later.");
        }else{
        	
        	this.setContentView(R.layout.toa_activity_table);
       
        	stationCode = null;
        	lastupdated = (TextView)findViewById(R.id.lastupdated);    
        	tv_station = (TextView)findViewById(R.id.toa_station);    
        	m_orders = new ArrayList<RailStationPrediction>();     
        	grid = (ListView)findViewById(R.id.gridview);
        	this.m_adapter = new ArrivalAdapter(this, R.layout.prediction_row, m_orders);
        	grid.setAdapter(m_adapter);

        	try {
        		Bundle bundle = this.getIntent().getExtras();
        		stationCode = bundle.getString("stationCode");
        		stationName = bundle.getString("stationName");
        		tv_station.setText(stationName);	
        	} catch (Exception e) {
        		Log.e(TAG, "error: " + e.toString());
        	}

    		m_orders = new ArrayList<RailStationPrediction>();
    		this.m_adapter = new ArrivalAdapter(this, R.layout.toa_activity_table, m_orders);
    		grid.setAdapter(this.m_adapter);  
        }
    }
	
	private Runnable mUpdateTimeTask = new Runnable() {
		
		@Override
		public void run() {
			
			update();
			
			mHandler.post(mUpdateResults);
			//mHandler.handleMessage(null);
			mHandler.postDelayed(mUpdateTimeTask, REFRESH_DELAY_MS_INT);
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
    	
		GregorianCalendar now = new GregorianCalendar();
		Date d =  now.getTime();
		DateFormat df1 = DateFormat.getDateTimeInstance();
		String s1 = df1.format(d);

		lastupdated.setText("Data refreshed every " + REFRESH_DELAY_MS_INT/1000 + " seconds.\nCurrent as of " + s1);
		
		if(m_orders != null && m_orders.size() > 0){
			m_adapter.notifyDataSetChanged();
			m_adapter.clear();
			for(int i=0;i<m_orders.size();i++){
				m_adapter.add(m_orders.get(i));
			}               
		}else{	
			lastupdated.setText("No results returned from WMATA.com.");
		}
		m_adapter.notifyDataSetChanged();
    }
	
	@Override
	public void onResume() {
		super.onResume();

    	if (m_ProgressDialog != null){
    		m_ProgressDialog.dismiss();
    	}
    	
		ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

		if (AppHelper.isConnected(connec)){

			m_ProgressDialog = new ProgressDialog(this);
			m_ProgressDialog.setTitle("Please wait...");
			m_ProgressDialog.setMessage("Retrieving data ...");
			m_ProgressDialog.setCancelable(true);
			m_ProgressDialog.show();

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			REFRESH_DELAY_MS_STRING = prefs.getString("REFRESH_DELAY_MS", "30000");

			Thread thread = new Thread(mUpdateTimeTask);
			thread.start();

			try{
				REFRESH_DELAY_MS_INT = Integer.parseInt(REFRESH_DELAY_MS_STRING);
			}catch(Exception ex){
				Log.e(TAG, "cannot parse REFRESH_DELAY_MS" + ex.toString());
			}
		}
	}

	private void update(){
		
     	ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	
     	if (AppHelper.isConnected(connec)){

     		try{
     			XmlPullFeedParser xmlpull = XmlPullFeedParser.getXmlPullFeedParser();
     			//get Lines by StationCode... some stations have more than one code!
     			// TODO: is there a better way to do this than re-parse the XML every refresh?
     			ArrayList<String> codes = xmlpull.getStationCodesByStation(stationCode, this);

     			m_orders = (ArrayList<RailStationPrediction>) xmlpull.parseStationPredictionByStation(codes);
     			//sort m_orders based on line code
     			Collections.sort(m_orders, new CustomComparator());

     			//Log.i("ARRAY", ""+ m_orders.size());
     		} catch (Exception e) {
     			Log.e("BACKGROUND_PROC", e.getMessage());
     		}
     		//runOnUiThread(returnRes);

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

	public class CustomComparator implements Comparator<RailStationPrediction> {
		@Override
		public int compare(RailStationPrediction o1, RailStationPrediction o2) {
			if (o1.getLine() == null){
				o1.setLine("No Passengers");
			} 
			if (o2.getLine() == null){
				o2.setLine("No Passengers");
			}
			
			return o1.getLine().compareTo(o2.getLine());
		}
	} 
	
	private class ArrivalAdapter extends ArrayAdapter<RailStationPrediction> {

		//@Override
		//public boolean areAllItemsEnabled(){
		//	return false;
		//}

		@Override
		public boolean isEnabled(int position){
			return false;
		}
		
		private ArrayList<RailStationPrediction> items;

		public ArrivalAdapter(Context context, int textViewResourceId, ArrayList<RailStationPrediction> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.prediction_row, null);
			}
			
			RailStationPrediction o = items.get(position);
			
			if (o != null) {
				
				ImageView iv1 = (ImageView)v.findViewById(R.id.icon1);
				if (o.getLine().equalsIgnoreCase("GR")){
					iv1.setImageResource(R.drawable.icon_marble_green);
				}else if (o.getLine().equalsIgnoreCase("YL")){
					iv1.setImageResource(R.drawable.icon_marble_yellow);
				}else if (o.getLine().equalsIgnoreCase("RD")){
					iv1.setImageResource(R.drawable.icon_marble_red);
				}else if (o.getLine().equalsIgnoreCase("BL")){
					iv1.setImageResource(R.drawable.icon_marble_blue);
				}else if (o.getLine().equalsIgnoreCase("OR")){
					iv1.setImageResource(R.drawable.icon_marble_orange);
				}
				
				TextView destination = (TextView) v.findViewById(R.id.destination);
				destination.setText(o.getDestinationName());
	
				TextView min = (TextView) v.findViewById(R.id.minutes);
				min.setText(o.getMin());
				

			}
			return v;
		}
	}
}