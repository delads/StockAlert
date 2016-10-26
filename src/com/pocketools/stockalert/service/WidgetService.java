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
import android.net.Uri;
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

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.pocketools.stockalert.R;
import com.pocketools.stockalert.StockDetail;
import com.pocketools.stockalert.URLFetcher;
import com.pocketools.stockalert.Widget;

 
public class WidgetService extends WidgetWakefulIntentService {
	

	public static final String ANALYTICS_UA_NUMBER = "UA-12361531-3"; 
	
	public WidgetService() {
		super("WidgetService");
	}
	
	private void updateWidget(){
		
		//Log.d("WidgetService.updateWidget()","Widget Service called");
		
		
		
		 SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
  		 SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	   	 Date date = new Date();

	   	 
	    SharedPreferences prefs = getApplicationContext().getSharedPreferences("widget_preferences", 0);
	    Map<String,?> widgetMap = prefs.getAll();
	    
	    Set<String> symbols = widgetMap.keySet();
	    Iterator<String> iter = symbols.iterator();
	    
	    while(iter.hasNext()){
	    	String symbol = (String)iter.next();
	    	int widgetId = (Integer)widgetMap.get(symbol);
	    	
	    	
	    	//Push latest information to Widget
		   	 RemoteViews updateViews = new RemoteViews(this.getPackageName(),R.layout.widget_layout);
		   	 
		   	 
		   //Fix for the DOW
        	if(symbol.compareToIgnoreCase("^DJI") == 0){
        		symbol = "INDU";
        	}

  		   	//Let's download
  		   	String url = "http://finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=l1c1p2n";
  		   	
  	//	Log.d("StockAlert WidgetService","GETTING UPDATE FOR " + symbol);
  		   	String result = URLFetcher.getString(url);
    	
	   		 
		   	 String timeString = timeFormat.format(date); 
		   	 String dateString = dateFormat.format(date);
		   	 
		   	 
		   	 String price = "";
		   	 String changeInPrice = "";
		   	 String changeInPricePercentage = "";
		   	 String companyName = "";

		   	 
	    	if(result != null){ 	    		
	    		try{
	    		
		    		StringTokenizer tokenizer = new StringTokenizer(result, "\n");
			    	 while(tokenizer.hasMoreTokens()){
			    		 String token = tokenizer.nextToken();	    		 
			    		 
			    		 StringTokenizer subtokenizer = new StringTokenizer(token,",");	       		 
			    		 price = subtokenizer.nextToken().replaceAll("\"", "");
			    		 changeInPrice = subtokenizer.nextToken().replaceAll("\"", "");
			    		 changeInPricePercentage = subtokenizer.nextToken().replaceAll("\"", "");
			    		 changeInPricePercentage = changeInPricePercentage.trim();
			    		 companyName = subtokenizer.nextToken().replaceAll("\"", "");
			    		 companyName = companyName.trim();

			    		 
			    	 }	
			    
			    	 //Let's make the currencies a little more readable and replace it
			    	 //with the "company name"
			    	 if(symbol.endsWith("=X"))
			    		 updateViews.setTextViewText(R.id.widgetStockSymbol, companyName);
			    	 else{
			    		 
			    		//Bug fix for DOW Jones
			    		 if(symbol.compareToIgnoreCase("INDU") ==0)
			    			 symbol = "^DJI";
			    		 
			    		 updateViews.setTextViewText(R.id.widgetStockSymbol, symbol);
			    	 }
			  
		    	
		   		   	updateViews.setTextViewText(R.id.widgetPrice, price);
		   		   	updateViews.setTextViewText(R.id.widgetLastUpdatedTime, timeString);
		   		   	updateViews.setTextViewText(R.id.widgetLastUpdatedDate, dateString);
		   		   	
		   		   	
		   		   	updateViews.setTextViewText(R.id.widgetPriceChangeAbsolute, changeInPrice);
		  		   	if(changeInPrice.startsWith("-"))
		  		   		updateViews.setTextColor(R.id.widgetPriceChangeAbsolute, Color.RED);
		  		   	else
		  		   		updateViews.setTextColor(R.id.widgetPriceChangeAbsolute, Color.GREEN);
		  		   	
		  		   	updateViews.setTextViewText(R.id.widgetPriceChangePercentage, changeInPricePercentage);
		  		   	if(changeInPricePercentage.startsWith("-"))
				   		updateViews.setTextColor(R.id.widgetPriceChangePercentage, Color.RED);
				   	else
				   		updateViews.setTextColor(R.id.widgetPriceChangePercentage, Color.GREEN);
		  		   	
		  		   	
		  		   	//Let's reset the Widget onClick Event since it seems to be falling off
	        	    Intent pIntent = new Intent(this,StockDetail.class);
	                Uri uri = Uri.fromParts("custom", "stockalert", symbol);
	                
	                pIntent.setData(uri);
	                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, pIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    		   	updateViews.setOnClickPendingIntent(R.id.widgetRelativeLayout, pendingIntent);

	    		   	
	    	    	
	    		   	
			   		// Push update for this widget to the home screen
		   		     ComponentName thisWidget = new ComponentName(this, Widget.class);
		   		     AppWidgetManager manager = AppWidgetManager.getInstance(this);
		   		     //int[] widgetIds = manager.getAppWidgetIds(thisWidget);

		   		    // manager.updateAppWidget(thisWidget, updateViews);
		   		     //Log.d("StockAlert - WidgetService", " manager.updateAppWidget(widgetId, updateViews) ");
		   		     manager.updateAppWidget(widgetId, updateViews);     
		    	 
	    		}catch(Exception e){
	    			//Log.e("WidgetService",e.getMessage());
	    		}
	    	}
	    	
	    	
	    	
	    	
	    }
    

	}
	 
	@Override
	protected void doWakefulWork(Intent intent) {
		
    	//Let's check the network status once more
    	ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = serviceConn.getActiveNetworkInfo();
	    
	    if(networkInfo != null)
	    	updateWidget();
	    else
	    	Log.d("WidgetService", "Alarm called but no NetworkInfo");
	
	}
	
		  
}
