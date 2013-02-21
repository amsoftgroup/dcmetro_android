package com.sombrerosoft.android.wmata;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.sombrerosoft.android.wmata.R;
import com.sombrerosoft.android.wmata.R.id;
import com.sombrerosoft.android.wmata.R.layout;
import com.sombrerosoft.android.wmata.R.menu;
import com.sombrerosoft.android.wmata.R.string;
import com.sombrerosoft.android.wmata.dal.XmlPullFeedParser;
import com.sombrerosoft.android.wmata.eula.Eula;
import com.sombrerosoft.android.wmata.helper.AppHelper;
import com.sombrerosoft.wmata.beans.*;
import com.sombrerosoft.wmata.beans.metro.Line;
import com.sombrerosoft.wmata.beans.metro.RailIncident;

public class LineActivity extends Activity{
	
	private ListView lv;
    private ProgressDialog m_ProgressDialog = null;
    private ArrayList<Line> m_orders = null;
    private OrderAdapter m_adapter;
    private ArrayList<RailIncident> ri;
    private TextView incidents;
    private String SYSTEM_WIDE_DELAY = "0";    
    private SharedPreferences prefs = null;
    private String TAG = "LineActivity";
    
    
	private Handler mHandler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	    	super.handleMessage(msg);
	    }
	};
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		Eula.show(this);
        setContentView(R.layout.main);
        //getOrders();
        //updateResultsInUi();
    }
    
	private Runnable mUpdateTimeTask = new Runnable() {

		public void run() {	
			getOrders();
			mHandler.post(mUpdateResults);
			//mHandler.postDelayed(mUpdateTimeTask, 10000);
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

    	String s = "";

    	if (SYSTEM_WIDE_DELAY.equals("1")){

    		if (ri != null){
    			if (ri.size() > 0){

    				String result = "";

    				for (int i=0; i < ri.size(); i++){
    					if (ri.get(i).getLinesAffected() != null){
    						
    						int l = ri.get(i).getLinesAffected().length;
    						
    						if (l > 0) {
    							
    							String[] del = new String[l];
    							result = ri.get(i).getLinesAffected()[0];    // start with the first element
    							for (int j=1; j<del.length; j++) {
    								result += ri.get(i).getLinesAffected()[j];
    							}
    						}
    					}
    					s +=  result + ri.get(i).getDescription() + " ... ";			
    				}
    			}else{
    				// TODO: perhaps center justify?
    				s = "No delays reported.";
    			}		
    		}
    	}else if (SYSTEM_WIDE_DELAY.equals("0")){
    		s = "";
    	}
    	
    	incidents.setText(s);
    	
    	if(m_orders != null && m_orders.size() > 0){
    		m_adapter.notifyDataSetChanged();
    		//m_adapter.clear(); ohhh, don't do that on this screen!
    		for(int i=0;i<m_orders.size();i++){
    			m_adapter.add(m_orders.get(i));
    		}               
    	}

    	m_adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
    	super.onStart();	

    	m_ProgressDialog = new ProgressDialog(this);
		m_ProgressDialog.setTitle("Please wait...");
		m_ProgressDialog.setMessage("Retrieving data ...");
		m_ProgressDialog.setCancelable(true);
		m_ProgressDialog.show();
		

/* 
		hscroller = (HorizontalScrollView) findViewById(R.id.hscroller);
		hscroller.scrollTo(100,200);
		hscroller.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
		hscroller.fling(5);
*/	


    }
    
    @Override public void onResume(){
    	super.onResume();

/*
    	busbutton = (Button)this.findViewById(R.id.busbutton);
    	busbutton.setText("Get Bus Lines");
    	busbutton.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			Intent newIntent = new Intent(LineActivity.this, BusActivity.class);	
    			startActivity(newIntent);
    		}
    	});
*/
    	prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	SYSTEM_WIDE_DELAY = prefs.getString("SYSTEM_WIDE_DELAY", "0"); 

    	m_orders = new ArrayList<Line>();
    	m_adapter = new OrderAdapter(this, R.layout.row, m_orders);
    	lv = (ListView)this.findViewById(android.R.id.list);  
    	incidents = (TextView)findViewById(R.id.delays);
    	lv.setAdapter(this.m_adapter);       
    	ri = new ArrayList<RailIncident>(); 
    	lv.setOnItemClickListener(new OnItemClickListener() {

    		@Override
    		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
    				long arg3) {
    			Intent newIntent = new Intent(LineActivity.this, StationActivity.class);
    			Line l = (Line) lv.getItemAtPosition(arg2);	
    			newIntent.putExtra("lineCode", l.getLineCode());	
    			m_orders = null;
    			startActivity(newIntent);
    		}
    	});

    	m_orders = new ArrayList<Line>();
    	this.m_adapter = new OrderAdapter(this, R.layout.row, m_orders);
    	lv.setAdapter(this.m_adapter);  

    	Thread t = new Thread(mUpdateTimeTask);
    	t.start();		
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
/*
    	case R.string.GPS_closest_bus:
    	{
    		Intent settingsActivity = new Intent(getBaseContext(), BusLocationActivity.class);
    		startActivity(settingsActivity);
    	}

    	return true;
*/
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
        case R.string.settings:
        {
        	Intent settingsActivity = new Intent(getBaseContext(), WMATAPreferenceActivity.class);
        	startActivity(settingsActivity);
        }
        return true;
        case R.string.status:
        {
        	Intent settingsActivity = new Intent(getBaseContext(), WebStatusActivity.class);
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

    private void getOrders(){

    	try{        	  
    		XmlPullFeedParser xmlpull = XmlPullFeedParser.getXmlPullFeedParser(this);
            //m_orders = (ArrayList<Line>) xmlpull.parseLines();
    		m_orders = (ArrayList<Line>)  xmlpull.parseHardCodedLines(this);
    		
/*
    		Line busses = new Line();
    		busses.setDisplayName("DC Bus System");
    		busses.setLineCode("BUS");
    		busses.setStartStationCode("BUS");
    		m_orders.add(busses);
  */  		
    		if (SYSTEM_WIDE_DELAY.equals("1")){
    			// only attempt to get data if we're connected
    			ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    			if (AppHelper.isConnected(connec)){
    				
    				WMATAApp appState = ((WMATAApp)getApplicationContext());
    				ArrayList<RailIncident> cachedDelay = appState.getRailIncidents();
    				if (cachedDelay==null){
    					ri = (ArrayList<RailIncident>) xmlpull.parseRailIncident();
    					appState.setRailIncidents(ri);
    				}else{
    					ri = cachedDelay;
    				}
    			}
    		}
    		
    	} catch (Exception e) {
    		Log.e(TAG, e.getMessage());
    	}
    }
    
    private class OrderAdapter extends ArrayAdapter<Line> {

        private ArrayList<Line> items;

        public OrderAdapter(Context context, int textViewResourceId, ArrayList<Line> items) {
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
            Line o = items.get(position);
            if (o != null) {
            	TextView tt = (TextView) v.findViewById(R.id.toptext);
            	ImageView iv = (ImageView) v.findViewById(R.id.icon);
                if (tt != null) {
                	tt.setText("  " + o.getDisplayName()); 
                	if (o.getLineCode().equalsIgnoreCase("BL")){
                		tt.setTextColor(Color.BLUE);
                	} else if (o.getLineCode().equalsIgnoreCase("YL")){
                		tt.setTextColor(Color.YELLOW);
                	} else if (o.getLineCode().equalsIgnoreCase("GR")){
                		tt.setTextColor(Color.GREEN);
                	} else if (o.getLineCode().equalsIgnoreCase("OR")){
                		tt.setTextColor(Color.rgb(255, 140, 0));
                	} else if (o.getLineCode().equalsIgnoreCase("RD")){
                		tt.setTextColor(Color.RED);
                	/*} else if (o.getLineCode().equalsIgnoreCase("BUS")){
                		tt.setTextColor(Color.GRAY);
                		tt.setBackgroundColor(Color.BLACK);
                		tt.setGravity(Gravity.CENTER);
                		*/
                	} else{
                		tt.setTextColor(Color.GRAY);
                	} 		
                }
            }
            return v;
        }
    }
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(mUpdateTimeTask);

	}
    
}