package com.sombrerosoft.android.wmata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.sombrerosoft.android.wmata.helper.AppHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class WebStatusActivity extends Activity {

	private TextView tvStatus;
    private ProgressDialog m_ProgressDialog = null;
    private String http_loc = "http://tequila.sombrerosoft.com/application/android/dcmetro/status/index.html";
	private Handler mHandler = new Handler();
	private String TAG = "WebStatusActivity";
	private String html = "";
	
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };
	private Runnable mUpdateTimeTask = new Runnable() {
		
		public void run() {	
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(http_loc);
			HttpResponse response = null;
			
			try {
				response = client.execute(request);
			} catch (ClientProtocolException e1) {
				Log.e(TAG, "err " + e1.toString());
			} catch (IOException e1) {
				Log.e(TAG, "err " + e1.toString());
			}

			InputStream in = null;
			try {
				in = response.getEntity().getContent();
			} catch (IllegalStateException e) {
				Log.e(TAG, "err " + e.toString());
			} catch (IOException e) {
				Log.e(TAG, "err " + e.toString());
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			try {
				while((line = reader.readLine()) != null)
				{
				    str.append(line);
				}
			} catch (IOException e) {
				Log.e(TAG, "err " + e.toString());
			}
			try {
				in.close();
			} catch (IOException e) {
				Log.e(TAG, "err " + e.toString());
			}
			html = str.toString();
			mHandler.post(mUpdateResults);
		}
	};	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	m_ProgressDialog = new ProgressDialog(this);
		m_ProgressDialog.setTitle("Please wait...");
		m_ProgressDialog.setMessage("Retrieving data ...");
		m_ProgressDialog.setCancelable(true);
		m_ProgressDialog.show();


    }
    
	@Override
	public void onPause(){
		super.onPause();
		html = "";
	}
	
	@Override
	public void onResume(){
		super.onResume();

        
     	ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
     	if (!AppHelper.isConnected(connec))
     	{
    		if (m_ProgressDialog != null){
    			m_ProgressDialog.dismiss();
    		}
    		
     		this.setContentView(R.layout.error);
     		
        	TextView destination = (TextView) findViewById(R.id.error);
        	destination.setText("No network connection!\nPlease try again later.");
        	
     	}else{	
    		
            setContentView(R.layout.web_status_view);
            tvStatus = (TextView)findViewById(R.id.tvStatus);
            
			Thread t = new Thread(mUpdateTimeTask);
			t.start();	
     	}
	}
    
	private void updateResultsInUi() {
	
		if (m_ProgressDialog != null){
			m_ProgressDialog.dismiss();
		}
		
		tvStatus.setText("\n\n" + Html.fromHtml(html));
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
