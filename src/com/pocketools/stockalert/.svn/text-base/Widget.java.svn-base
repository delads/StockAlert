package com.pocketools.stockalert;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.pocketools.stockalert.service.OnWidgetAlarmReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.Uri;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Define a simple widget that shows the Wiktionary "Word of the day." To build
 * an update we spawn a background {@link Service} to perform the API queries.
 */
public class Widget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
        int[] appWidgetIds) {  	
    	
    	
		for(int i=0;i<appWidgetIds.length;i++){
			
			int id = appWidgetIds[i];
			
			SharedPreferences prefs = context.getSharedPreferences("widget_preferences", 0);
    	    Map<String,?> widgetMap = prefs.getAll();
    	    
    	    Set<String> symbols = widgetMap.keySet();
    	    Iterator<String> iter = symbols.iterator();
    	    
    	    String symbolFound = "";
    	    
    	    while(iter.hasNext()){
    	    	String symbol = iter.next();
    	    	int widget = (Integer) widgetMap.get(symbol);
    	    	if (widget == id){
    	    		symbolFound = symbol;
    	    	}      	    	
    	    }
    	    
    	    if(symbolFound.length() > 1){
        	    
        	    Intent pIntent = new Intent(context,StockDetail.class);
                Uri uri = Uri.fromParts("custom", "stockalert", symbolFound);
                
                pIntent.setData(uri);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, pIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                
                RemoteViews updateViews = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
    		   	updateViews.setOnClickPendingIntent(R.id.widgetRelativeLayout, pendingIntent);
    		   	
    		   	AppWidgetManager.getInstance(context).updateAppWidget(id, updateViews);
            
    	    }
    	    
		}
		
    	super.onUpdate(context, appWidgetManager, appWidgetIds);
         
    }
    
    @Override
    public void onDeleted (Context context, int[] appWidgetIds){
    	 	
	
		SharedPreferences prefs = context.getSharedPreferences("widget_preferences", 0);
	    Map<String,?> widgetMap = prefs.getAll();
	    
	    Set<String> symbols = widgetMap.keySet();
	    Iterator<String> iter = symbols.iterator();
	    
	    iter.next();//Skip the Widget currently being deleted
	    
	    if(! iter.hasNext()){//This is our final Widget. Let's shut down the widget alarm
    		
    	
	    	AlarmManager mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent alarmIntent= new Intent(context, OnWidgetAlarmReceiver.class);
			PendingIntent pi=PendingIntent.getBroadcast(context, 0,alarmIntent, 0);			
	    	mgr.cancel(pi);
	    	
	    	//Log.d("Stock Alert Pro, Widget.onDeleted()","Widget Alarm now Cancelled");

	    	
    	}
    	
    	super.onDeleted(context,appWidgetIds);
    	
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
            //all the intents get handled by this method
            //mainly used to handle self created intents, which are not
            //handled by any other method
           
           
            //the super call delegates the action to the other methods
           
            //for example the APPWIDGET_UPDATE intent arrives here first
            //and the super call executes the onUpdate in this case
            //so it is even possible to handle the functionality of the
            //other methods here
            //or if you don't call super you can overwrite the standard
            //flow of intent handling
    	final String action = intent.getAction();
    	
    	
    	
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)){
            
        	final int appWidgetId = intent.getExtras().getInt (AppWidgetManager.EXTRA_APPWIDGET_ID,
                    								AppWidgetManager.INVALID_APPWIDGET_ID);
        	
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                this.onDeleted(context, new int[] { appWidgetId });
                
                SharedPreferences prefs = context.getSharedPreferences("widget_preferences", 0);
        	    Map<String,?> widgetMap = prefs.getAll();
        	    
        	    Set<String> symbols = widgetMap.keySet();
        	    Iterator<String> iter = symbols.iterator();
        	    
        	    String symbolFound = "";
        	    
        	    while(iter.hasNext()){
        	    	String symbol = iter.next();
        	    	int widget = (Integer) widgetMap.get(symbol);
        	    	if (widget == appWidgetId){
        	    		symbolFound = symbol;
        	    	}      	    	
        	    }
        	    
        	    Editor edit = prefs.edit();
        	    edit.remove(symbolFound);
        	    edit.commit();
                
            }
            
        } else {
            super.onReceive(context, intent);
        }
    }
}
