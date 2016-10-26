/***
Copyright (c) 2009 CommonsWare, LLC
Licensed under the Apache License, Version 2.0 (the "License"); you may
not use this file except in compliance with the License. You may obtain
a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
 
package com.pocketools.stockalert.service;
 
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.pocketools.stockalert.DBAdapter;
import com.pocketools.stockalert.R;
import com.pocketools.stockalert.StockAlert;
import com.pocketools.stockalert.StockDetail;
import com.pocketools.stockalert.URLFetcher;
import com.pocketools.stockalert.Widget;

 
public class AppService extends WakefulIntentService {
	
	private static final int  NOTIFICATION = 1000;
	public static final String ANALYTICS_UA_NUMBER = "UA-12361531-3"; 
	
	public AppService() {
		super("AppService");
	}
	
	private void checkAlerts(){
		
		//Log.d("AppService.checkAlerts()","App Service called");
		
		 SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
  		 SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	   	 Date date = new Date();
		
	    Cursor alerts = null;
	    
        DBAdapter db = new DBAdapter(this);
        db.open();
        
	    alerts = db.getActiveAlerts();
	    
	    if(alerts.getCount() > 0){
	    	
	    	//Log.d("AppService.checkAlerts()","System has found " + alerts.getCount() + "Alerts");
	    	
		    Map<String,Alert> alertMap = new HashMap<String,Alert>();
		    
		    StringBuffer symbolList = new StringBuffer();
		    boolean hasDJI = false;
		    Alert alertDJI = null;
		    
		    for(int i=0;i<alerts.getCount();i++){
		    	alerts.moveToPosition(i);
		    	
		    	Alert alert = new Alert();
		    	
		    	String symbol =  alerts.getString(alerts.getColumnIndex(DBAdapter.KEY_SYMBOL));
	        	
		    	
		    	//Do not want to get alerts from Yahoo finance for DJI anymore. 
		    	
		    	/*
	        	//Fix for the DOW
	        	if(symbol.compareToIgnoreCase("^DJI") == 0){
	        		symbol = "INDU";
	        	}
	        	*/
		    	
		    	alert.symbol = symbol;
		    	
		    	
		    	if(alerts.getInt(alerts.getColumnIndex(DBAdapter.KEY_HIGH_ALERT_ACTIVE)) == 1){
		    		alert.highPriceAlertSet = true;
		    		alert.highPrice = Double.parseDouble(alerts.getString(alerts.getColumnIndex(DBAdapter.KEY_HIGH_ALERT_PRICE)));
			    	
		    	}
		    	else
		    		alert.highPriceAlertSet = false;
		    	
		    	if(alerts.getInt(alerts.getColumnIndex(DBAdapter.KEY_LOW_ALERT_ACTIVE)) == 1){
		    		alert.lowPriceAlertSet = true;
		    		alert.lowPrice = Double.parseDouble(alerts.getString(alerts.getColumnIndex(DBAdapter.KEY_LOW_ALERT_PRICE)));
			    	
		    	}
		    	else
		    		alert.lowPriceAlertSet = false;
		    	
		    	
		    	//We do not want to check Yahoo Finance for DJI. Will do this seperately
	    		if(symbol.compareToIgnoreCase("^DJI") == 0){
	    			hasDJI = true;
	    			alertDJI = alert;
	    		}
	    		else{
	    			if(i == 0)
			    		symbolList.append(alert.symbol);
			    	else
			    		symbolList.append("+" + alert.symbol);
	    			
	    			alertMap.put(alert.symbol,alert);
	    		}

		    }
	    				

	    	String url = "http://finance.yahoo.com/d/quotes.csv?s=" + symbolList.toString() + "&f=sl1";
	    	//Log.d("AppService.checkAlerts()","URL = " + url);
	    	
	    	String result = URLFetcher.getString(url);
	    	
	    	//Log.d("AppService.checkAlerts()","HTTP Response = " + result);
	    		    	
	    	if(result != null){
	    		
	    		try{
	    		
		    		StringTokenizer tokenizer = new StringTokenizer(result, "\n");
			    	 while(tokenizer.hasMoreTokens()){
			    		 String token = tokenizer.nextToken();	    		 
			    		 
			    		 StringTokenizer subtokenizer = new StringTokenizer(token,",");	   
			    		 
			    		 String symbol = subtokenizer.nextToken().replaceAll("\"", "");	    		 
			    		 String price = subtokenizer.nextToken().replaceAll("\"", "");
			    		 double currentPrice = Double.parseDouble(price);
			    		 
			    		 Alert alert = alertMap.get(symbol);
			    		 if(alert != null){
			    			 
			    			 if(alert.highPriceAlertSet){
			    	    			if(currentPrice > alert.highPrice){
			    	    				
			    	    				/*
			    	    				
			    	    				//Bug fix for DOW Jones
			    			    		 if(symbol.compareToIgnoreCase("INDU") ==0)
			    			    			 symbol = "^DJI";
										*/
			    	    				
			    	    				//This is where we send an alert to the user
			    	    				db.activateHighAlert(symbol, false);
			    	    				//Log.d("AppService","Deactivating High Alert");
			    	    				
			    	    				db.addAlertMessage(symbol, "High Alert Triggered at " + alert.highPrice + " on " + dateFormat.format(date) + ":" + timeFormat.format(date));
			    	    				//Log.d("AppService","Adding High Alert Message to DB");
			    	    				
			    	    				startAlertNotification(symbol);	
			    	    				//Log.d("AppService","Starting notification");
			    	    				
			    	    				GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();        
			    	    		        // Start the tracker in manual dispatch mode...
			    	    		        tracker.startNewSession(ANALYTICS_UA_NUMBER, this);
			    	    		        tracker.trackPageView("/AlertTriggered");
			    	    		        tracker.dispatch();
			    	    		        //Log.d("AppService","Dispatching Analytics");
			    	    		        
			    	    			}
			    	    		}	    	    		
			    	    		if(alert.lowPriceAlertSet){
			    	    			if(currentPrice < alert.lowPrice){
			    	    				
			    	    				
			    	    				//We don't want triggers to go off for DJI results from Yahoo
			    	    				/*
			    	    				//Bug fix for DOW Jones
			    			    		 if(symbol.compareToIgnoreCase("INDU") ==0)
			    			    			 symbol = "^DJI";
										*/
			    	    				
			    	    				//This is where we send an alert to the user
			    	    				db.activateLowAlert(symbol, false);
			    	    				//Log.d("AppService","Deactivating Low Alert");
			    	    				
			    	    				db.addAlertMessage(symbol, "Low Alert Triggered at " + alert.lowPrice + " on " + dateFormat.format(date) + ":" + timeFormat.format(date));
			    	    				//Log.d("AppService","Adding High Alert Message to DB");
			    	    				
			    	    				startAlertNotification(symbol);
			    	    				//Log.d("AppService","Starting notification");
			    	    				
			    	    				GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();        
			    	    		        // Start the tracker in manual dispatch mode...
			    	    		        tracker.startNewSession(ANALYTICS_UA_NUMBER, this);
			    	    		        tracker.trackPageView("/AlertTriggered");
			    	    		        tracker.dispatch();
			    	    		        //Log.d("AppService","Dispatching Analytics");
			    	    			}
			    	    		}	    			 
			    		 }
			    	 }	
		    	 
	    		}catch(Exception e){
	    			
	    		}
	    	}
	    	
	    	
	    	
	    	
	    	/*
	    	 * If we are looking at DJI - then we need to goto Google Finance API
	    	 */
	    	
	    	if(hasDJI){
		    	String googleURL = "http://www.google.com/finance/info?q=^DJI";
		    	//Log.d("Stock Alert - AlertService",googleURL);
	
		   		 String jsonResult = URLFetcher.getString(googleURL);	    		 
		   		 
		   		 
		   		 double currentPrice = 0.0;
		   		 
		   		 if(jsonResult != null){
		   			 
		   			String trimmedResult = jsonResult.replace("// ", "");
		   			
		   			 try{
		   				 
		   				 JSONArray array = new JSONArray(trimmedResult);
		   				 
		   				//Let's loop through first and fetch all thumbnails
			    		 for(int i=0;i< array.length();i++){
			    			 			    			 
			    			 JSONObject element = array.getJSONObject(i);
			    			 String price = element.getString("l");
			    			 
			    			 //Let's do a fix for the comma
			    			 String priceWithoutComma = price.replace(",", "");
			    			 currentPrice = Double.parseDouble(priceWithoutComma);
			    			 
			    			 

			    				 if(alertDJI != null && alertDJI.highPriceAlertSet){
			    												 
			    					 if(currentPrice > alertDJI.highPrice){
				    	    				
				    	    				//This is where we send an alert to the user
				    	    				db.activateHighAlert("^DJI", false);
				    	    				//Log.d("AppService","Deactivating High Alert");
				    	    				
				    	    				db.addAlertMessage("^DJI", "High Alert Triggered at " + alertDJI.highPrice + " on " + dateFormat.format(date) + ":" + timeFormat.format(date));
				    	    				//Log.d("AppService","Adding High Alert Message to DB");
				    	    				
				    	    				startAlertNotification("^DJI");	
				    	    				//Log.d("AppService","Starting notification");
				    	    				
				    	    				GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();        
				    	    		        // Start the tracker in manual dispatch mode...
				    	    		        tracker.startNewSession(ANALYTICS_UA_NUMBER, this);
				    	    		        tracker.trackPageView("/AlertTriggered");
				    	    		        tracker.dispatch();
				    	    		        //Log.d("AppService","Dispatching Analytics");
				    	    		        
				    	    			} // end if(currentPrice > alertDJI.highPrice){
			    				 }// end if(alertDJI != null && alertDJI.highPriceAlertSet){
		    				 

			    				 if(alertDJI != null & alertDJI.lowPriceAlertSet){
			    					 
				    	    			if(currentPrice < alertDJI.lowPrice){
				    	    				
				    	    				//This is where we send an alert to the user
				    	    				db.activateLowAlert("^DJI", false);
				    	    				//Log.d("AppService","Deactivating Low Alert");
				    	    				
				    	    				db.addAlertMessage("^DJI", "Low Alert Triggered at " + alertDJI.lowPrice + " on " + dateFormat.format(date) + ":" + timeFormat.format(date));
				    	    				//Log.d("AppService","Adding High Alert Message to DB");
				    	    				
				    	    				startAlertNotification("^DJI");
				    	    				//Log.d("AppService","Starting notification");
				    	    				
				    	    				GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();        
				    	    		        // Start the tracker in manual dispatch mode...
				    	    		        tracker.startNewSession(ANALYTICS_UA_NUMBER, this);
				    	    		        tracker.trackPageView("/AlertTriggered");
				    	    		        tracker.dispatch();
				    	    		        //Log.d("AppService","Dispatching Analytics");
				    	    			} // end if(currentPrice < alertDJI.lowPrice){
				    	    		}// end if(alertDJI != null & alertDJI.lowPriceAlertSet){	    	
				    				 
				    			 }	// end for loop 	    			 

		   			 }catch (JSONException e){
		   				 Log.d("AlertService","Exception thrown : " + e.getMessage());
		   				//throw new Exception("AlertService - Unable to Contact Google Finance for Stock Prices");
		   			 }
		   		 }	// end if(jsonResult != null){
	    	}// end if(hasDJI){
	    }
 
	    alerts.close();
	    db.close();	    

	}
	 
	@Override
	protected void doWakefulWork(Intent intent) {
		
    	//Let's check the network status once more
    	ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = serviceConn.getActiveNetworkInfo();
	    
	    if(networkInfo != null)
	    	checkAlerts();
	
	}
	
	
	private void startAlertNotification(String symbol){
    	
		/*
    	// Get the notification manager serivce.
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        
        Context context = getApplicationContext();
        CharSequence contentTitle = "Stock Alert";
        CharSequence contentText = "Stock Alert triggered for " + symbol;
        
        Intent notificationIntent = new Intent(this, StockDetail.class);
        notificationIntent.putExtra("SYMBOL", symbol);
        
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        notification.icon = R.drawable.stock_alert_notify_dollar;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND;
        
        notificationManager.notify(NOTIFICATION, notification);   
        */
		
		// Get the notification manager service.
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        int icon = R.drawable.alert_image;
        CharSequence tickerText = "Stock Alert triggered for " + symbol;
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);

        
        Context context = getApplicationContext();
        CharSequence contentTitle = "Stock Alert";
        CharSequence contentText = "Stock Alert Triggered";
        Intent notificationIntent = new Intent(this, StockAlert.class);
        //notificationIntent.putExtra("STILL_UPDATING", true);
        //notificationIntent.putExtra("COMPANY_NAME_START", "");
        //notificationIntent.putExtra("SYMBOL", symbol);
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_ALL;
        
        notificationManager.notify(NOTIFICATION, notification);

    }
	
	
		  
}
