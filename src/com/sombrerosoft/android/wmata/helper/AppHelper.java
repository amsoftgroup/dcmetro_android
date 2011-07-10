package com.sombrerosoft.android.wmata.helper;

import android.net.ConnectivityManager;

public class AppHelper {
	
	
	public static String resolveLineCode(String lineCode){
		
		String line = null;
		
		if (lineCode.equalsIgnoreCase("RD")){
			line = "Red Line";
		} else 	if (lineCode.equalsIgnoreCase("GR")){
			line = "Green Line";
		} else if (lineCode.equalsIgnoreCase("BL")){
			line = "Blue Line";
		} else if (lineCode.equalsIgnoreCase("YL")){
			line = "Yellow Line";
		} else if (lineCode.equalsIgnoreCase("OR")){
			line = "Orange Line";
		} else {
			line = lineCode;
		}
		return line;
	}

	public static boolean isConnected(ConnectivityManager cm){

//if ( cm.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||  cm.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED  );
		return  (cm.getActiveNetworkInfo() != null &&
				cm.getActiveNetworkInfo().isAvailable() &&
				cm.getActiveNetworkInfo().isConnected());

	}
	
}
