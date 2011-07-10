package com.sombrerosoft.android.wmata.eula;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Closeable;

import com.sombrerosoft.android.wmata.WMATAApp;

public class Eula {

	private static final String ASSET_EULA = "EULA";
	private static final String PREFERENCE_EULA_ACCEPTED = "eula.accepted";
	private static final String PREFERENCES_EULA = "eula";
	
	static interface OnEulaAgreedTo{
		void onEulaAgreedTo();
	}
	
	public static boolean show(final Activity activity){
		
		final SharedPreferences preferences = activity.getSharedPreferences(PREFERENCES_EULA, Activity.MODE_PRIVATE);
		
		//test code: uncomment to display EULA even if already agreed to
		//preferences.edit().putBoolean(PREFERENCE_EULA_ACCEPTED, false).commit();
		
		if (!preferences.getBoolean(PREFERENCE_EULA_ACCEPTED, false)){
			
			final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("End User License Agreement");
			builder.setCancelable(true);
			builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					accept(preferences);
					if (activity instanceof OnEulaAgreedTo){
						((OnEulaAgreedTo) activity).onEulaAgreedTo(); 
					}
					
				}
			});
			
			builder.setNegativeButton("Do Not Accept", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				refuse(activity);
			}
		});
		
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				refuse(activity);
			}
		});
		
		builder.setMessage(readEula(activity));
		builder.create().show();
		return false;
	}
		return true;
}
	
	private static void accept(SharedPreferences preferences){
		preferences.edit().putBoolean(PREFERENCE_EULA_ACCEPTED, true).commit();
	}
	
	private static void refuse(Activity activity){
		activity.finish();
	}
	
	private static CharSequence readEula(Activity activity){
		BufferedReader in = null;
		try{
			in = new BufferedReader(new InputStreamReader(activity.getAssets().open(ASSET_EULA)));
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = in.readLine()) != null){
				buffer.append(line).append('\n');
			}
			return buffer;
		}catch(IOException e){
			return "";
		}finally{
			closeStream(in);
		}
		
	}

	private static void closeStream(Closeable stream) {
		if (stream != null){
			try{
				stream.close();
			}catch(IOException e){
				Log.e("EULA", "CANNOT CLOSE STREAM:" + e.toString());	
			}
		}
		
	}

	public static void show(WMATAApp wmataApp) {
		// TODO Auto-generated method stub
		
	}

	
}
