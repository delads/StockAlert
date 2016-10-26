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
 
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.SystemClock;
import android.util.Log;

import com.pocketools.stockalert.DBAdapter;
import com.pocketools.stockalert.Markets;
 
public class OnBootReceiver extends BroadcastReceiver {

 
@Override
public void onReceive(Context context, Intent intent) {
	
	
    SharedPreferences prefs = context.getSharedPreferences("widget_preferences", 0);
    Map<String,?> widgetMap = prefs.getAll();
    
    Set<String> symbols = widgetMap.keySet();
    Iterator<String> iter = symbols.iterator();

    
    
    
    
	
	    if(iter.hasNext()){ //We reset the alarm following REBOOT
	    	
	    	 //This will create a new alarm - replacing the existing alarm if already present
			AlarmManager mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent alarmIntent=new Intent(context, OnWidgetAlarmReceiver.class);
			PendingIntent pi=PendingIntent.getBroadcast(context, 0,alarmIntent, 0);
			mgr.setRepeating(AlarmManager.RTC,SystemClock.elapsedRealtime()+60000,Markets.PERIOD,pi); 			
			Log.d("Stock Alert","We have a widget so the alarm is reset");    
	    }
    
	    
	    Cursor alerts = null;
	    
        DBAdapter db = new DBAdapter(context);
        db.open();
        
	    alerts = db.getActiveAlerts();
	    
	    if(alerts.getCount() > 0){
	
			AlarmManager mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent i=new Intent(context, OnAlarmReceiver.class);
			PendingIntent pi=PendingIntent.getBroadcast(context, 0,i, 0);
			 
			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+60000,Markets.PERIOD,pi);
			
			Log.d("Stock Alert Pro","Resetting the ALERTS Alarm for Stock Alert"); 
		
	    }
		    
		 db.close();   
	}
}









