package com.sombrerosoft.android.wmata;

import java.util.ArrayList;
import java.util.Comparator;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.sombrerosoft.android.wmata.R;
import com.sombrerosoft.android.wmata.R.id;
import com.sombrerosoft.android.wmata.R.layout;
import com.sombrerosoft.android.wmata.R.menu;
import com.sombrerosoft.android.wmata.R.string;
import com.sombrerosoft.android.wmata.dal.XmlPullFeedParser;
import com.sombrerosoft.wmata.beans.*;
import com.sombrerosoft.wmata.beans.metro.Station;

public class StationActivity extends ListActivity{
	
	private ListView lv;
	
	private String lineCode;
	private String stationCode;	
	
    private ProgressDialog m_ProgressDialog = null;
    private ArrayList<Station> m_orders = null;
    private OrderAdapter m_adapter;
    private Runnable viewOrders;
    private String FORMAT;
    private String TAG = "StationActivity";
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     
        m_orders = new ArrayList<Station>();
        this.m_adapter = new OrderAdapter(this, R.layout.row, m_orders);
        setListAdapter(this.m_adapter);

		try {
			Bundle bundle = this.getIntent().getExtras();
			if ((bundle.containsKey("lineCode")) && (bundle.getString("lineCode") != null)){
				lineCode = bundle.getString("lineCode");
			}
			if ((bundle.containsKey("stationCode")) && (bundle.getString("stationCode") != null)){
				stationCode = bundle.getString("stationCode");
			}			
		} catch (Exception e) {
			Log.e(TAG, "onCreate: " + e.toString());
		} 		
 
 
        viewOrders = new Runnable(){
            @Override
            public void run() {
                getOrders();
            }
        };
        
        m_ProgressDialog = ProgressDialog.show(StationActivity.this,    
                "Please wait...", "Retrieving data ...", true);
        
        Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
        thread.start();

    }
    

    
    private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
        	if(m_orders != null && m_orders.size() > 0){
        		m_adapter.notifyDataSetChanged();
                for(int i=0;i<m_orders.size();i++){
                    m_adapter.add(m_orders.get(i));	
                }
            } 
            m_adapter.notifyDataSetChanged();
            m_ProgressDialog.dismiss();
        }
    };
    
    private void getOrders(){
    	try{        	  
    		XmlPullFeedParser xmlpull = XmlPullFeedParser.getXmlPullFeedParser(this);
        	//m_orders = xmlpull.parseStationsByLineCode(lineCode);

    		m_orders = (ArrayList<Station>) xmlpull.parseHardCodedStationsByLine(this, lineCode);
    		
    		if (m_orders == null){
	    	    AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	            alertbox.setMessage("No internet connection to WMATA. Please try again.");
	            alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface arg0, int arg1) {
	                //    Toast.makeText(getApplicationContext(), "OK button clicked", Toast.LENGTH_LONG).show();
	                }
	            });
	            alertbox.show();
    		}
    		
    	} catch (Exception e) {
    		Log.e("BACKGROUND_PROC", e.getMessage());
    	}
    	runOnUiThread(returnRes);
    }
    
    @Override    
    public void onResume() {	
    	super.onResume();
   
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        FORMAT = prefs.getString("RESULT_FORMAT", "TABLE"); 
        this.lv = (ListView)this.findViewById(android.R.id.list);  
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
	        	Intent newIntent;
	        	
	        	if (FORMAT.equalsIgnoreCase("TEXT")){
	        		//Log.d(TAG, " FORMAT " + FORMAT );
	        		newIntent = new Intent(StationActivity.this, TimeOfArrivalActivityText.class);
	        	}else{
	        		//Log.d(TAG, " FORMAT " + FORMAT );
	        		newIntent = new Intent(StationActivity.this, TimeOfArrivalActivity.class);
	        	}
	        	
	        	Station s = (Station) lv.getItemAtPosition(arg2);

	        	newIntent.putExtra("lineCode", lineCode);
	        	newIntent.putExtra("stationCode", s.getCode());
	        	newIntent.putExtra("stationName", s.getName());

	        	m_orders = null;
	        	startActivity(newIntent);
			}
        });
                
        
    }
    
    private class OrderAdapter extends ArrayAdapter<Station> {

        private ArrayList<Station> items;

        public OrderAdapter(Context context, int textViewResourceId, ArrayList<Station> items) {
        	super(context, textViewResourceId, items);
        	this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	View v = convertView;	
        	if (v == null) {
        		LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        		v = vi.inflate(R.layout.row, null);
        	}
        	Station o = items.get(position);
        	if (o != null) {
        		TextView tt = (TextView) v.findViewById(R.id.toptext);

        		if (tt != null) {
        			tt.setText("  " + o.getName());                            
        		}
        	}
            return v;
        }
    }
    
	public class StationGeoComparator implements Comparator<Station> {
		@Override
		public int compare(Station o1, Station o2) {
			if (o1.getName() == null){
				o1.setName("---");
			} 
			if (o2.getName() == null){
				o2.setName("---");
			}
			
			return o1.getName().compareTo(o2.getName());
		}
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
}


