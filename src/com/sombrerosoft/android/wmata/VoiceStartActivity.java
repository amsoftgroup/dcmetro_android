package com.sombrerosoft.android.wmata;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.sombrerosoft.android.wmata.R;

public class VoiceStartActivity extends Activity implements OnClickListener {
  
	private Location mLocation;
	private LocationManager mLocationManager;
	private String sLocation = "";
	private boolean network_enabled = false;
    private boolean firstRun = true;
	private boolean isReady = true;
	private String TAG = "VoiceStartActivity";
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private String TRIP_PLANNER = "TRANSIT";    
    private SharedPreferences prefs = null;
    private String param = "";
    private ListView mList;
    
	private Handler mHandler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	    	super.handleMessage(msg);
	    }
	};
	private Runnable mUpdateTimeTask = new Runnable() {
		
		@Override
		public void run() {
			mHandler.post(mUpdateResults);
		}
	};
	
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };
    
    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.voice_recognition);
        Button speakButton = (Button) findViewById(R.id.btn_speak);
        
        mList = (ListView) findViewById(R.id.list);

        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            speakButton.setOnClickListener(this);
        } else {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
    }

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mUpdateTimeTask);
		mLocationManager.removeUpdates(myLocL);
	}	
	
	@Override    
	public void onResume() {	
		super.onResume();
		
    	prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	TRIP_PLANNER = prefs.getString("TRIP_PLANNER", "TRANSIT"); 
    	

    	
    	if (TRIP_PLANNER.equalsIgnoreCase("WALK")){
    		param = "dirflg=w";
    	}else if (TRIP_PLANNER.equalsIgnoreCase("DRIVE")){
    		param = "";
    	}else if (TRIP_PLANNER.equalsIgnoreCase("TRANSIT")){
    		param = "dirflg=r";
    	}
    	
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		try{
			network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			//Log.d(TAG, "NETWORK_PROVIDER enabled");
		}catch(Exception ex){
			Log.e(TAG, "NETWORK_PROVIDER not enabled: " + ex.toString());
		}   

		if (network_enabled){
			mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, (float) 100, myLocL); 	
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

        Thread thread = new Thread(mUpdateTimeTask);
        thread.start();  
	}

    public void onClick(View v) {
        if (v.getId() == R.id.btn_speak) {
            startVoiceRecognitionActivity();
        }
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please state the address of your destination.");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            mList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    matches));
            
            String s = "";
            for (int i = 0; i < matches.size();i++){
            	s += matches.get(i) + " ";
            }

            String url = "http://maps.google.com/maps?saddr=" + sLocation + "&daddr=" + s + "&" + param;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  Uri.parse(url));
            startActivity(intent);
            
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
    
  private void updateResultsInUi() {
    
    	
    	String firstRunS = "";
		if (firstRun){
			//firstRunS += "Using last known location (update pending):\n";
			firstRun = false;
		}else{
			//firstRunS += "Detected location:\n";
		}
		
        // Back in the UI thread -- update our UI elements based on the data in mResults

		String location = "";
		if (mLocation != null){
				
			List<Address> addresses;
			
			try{	
				Geocoder gc = new Geocoder(this, Locale.getDefault());
				addresses = gc.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
				
				if (addresses != null){
					Address currentAddress = addresses.get(0);
					StringBuilder sb = new StringBuilder(firstRunS);
					for (int i=0; i <currentAddress.getMaxAddressLineIndex(); i++){
						sb.append(currentAddress.getAddressLine(i)).append(" ");
					}
					sLocation = sb.toString();
				}
				
			}catch(Exception e){
				Log.e(TAG, e.toString());
				// fall back and provide lat/lons if Geocoder fails
				sLocation = mLocation.getLatitude() + ", " +  mLocation.getLongitude();
			}
		}
    }

	private final LocationListener myLocL = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {	
			mLocation = location;
			
	        Thread thread = new Thread(mUpdateTimeTask);
	        thread.start();
	        
		}
		@Override
		public void onProviderDisabled(String provider) {

		}
		@Override
		public void onProviderEnabled(String provider) {}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}	
	};
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
}
