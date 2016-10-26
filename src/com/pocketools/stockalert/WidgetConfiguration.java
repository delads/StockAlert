package com.pocketools.stockalert;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.pocketools.stockalert.R;
import com.pocketools.stockalert.service.OnWidgetAlarmReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class WidgetConfiguration extends Activity {
    /** Called when the activity is first created. */

   
    private String mSymbol;
    private Spinner mSpinnerLeft;
    private ArrayAdapter mLeftAdapter;
    private int mSpinnerLeftPosition = 0;
    DBAdapter mDb;
    ArrayList<String> stockArray;
    private int mAppWidgetId = 0;
    //AdView mAdView;
    private GoogleAnalyticsTracker mTracker;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        mTracker = GoogleAnalyticsTracker.getInstance();        
        // Start the tracker in manual dispatch mode...
        mTracker.start(StockAlert.ANALYTICS_UA_NUMBER, this);
        mTracker.trackPageView("/Widget");
        
        
     // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Set the view layout resource to use.
        setContentView(R.layout.widget_configuration);

        
        stockArray = new ArrayList<String>();
            
        mDb = new DBAdapter(this);
        mDb.open();
        
        Cursor stocks = mDb.getStocks();
        startManagingCursor(stocks);
        
        for(int i=0; i < stocks.getCount(); i++){
        	
        	stocks.moveToPosition(i);
        	      	
        	stockArray.add(stocks.getString(stocks.getColumnIndex(DBAdapter.KEY_SYMBOL)) + "   -  " + 
        			stocks.getString(stocks.getColumnIndex(DBAdapter.KEY_COMPANY_NAME)));
        }
       
       
       mSpinnerLeft = (Spinner)findViewById(R.id.graph_spinner_left);
       mLeftAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stockArray);
       mLeftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       mSpinnerLeft.setAdapter(mLeftAdapter);
       mSpinnerLeft.setSelection(mSpinnerLeftPosition);
       
       
       
       // This listener is used to set the selected timeframe from the Spinner.
       mSpinnerLeft.setOnItemSelectedListener(new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View v, int position,
             long id) {
				String longCurrency = (String)mLeftAdapter.getItem(position);
				mSymbol = longCurrency.substring(0,longCurrency.indexOf(" "));

		}
        public void onNothingSelected(AdapterView arg0) {
          // NOP
        }
      });
       
       Button saveButton = (Button)findViewById(R.id.widget_button);
       
       saveButton.setOnClickListener(new Button.OnClickListener() {
           public void onClick(View v) {
      	
           	//mDb.setWidgetStock(mSymbol);
      	           	
           	Intent intent = getIntent();
           	Bundle extras = intent.getExtras();
           	if (extras != null) {
           	    mAppWidgetId = extras.getInt(
           	            AppWidgetManager.EXTRA_APPWIDGET_ID, 
           	            AppWidgetManager.INVALID_APPWIDGET_ID);
           	}
           	
           	
           	//AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(v.getContext());
         // Create an Intent to launch ExampleActivity
            Intent pIntent = new Intent(v.getContext(),StockDetail.class);
            Uri uri = Uri.fromParts("custom", "stockalert", mSymbol);
            
            pIntent.setData(uri);
            PendingIntent pendingIntent = PendingIntent.getActivity(v.getContext(), 0, pIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //PendingIntent.gets
           
        	 
           	
          //Push latest information to Widget
		   	RemoteViews updateViews = new RemoteViews(v.getContext().getPackageName(),R.layout.widget_layout);
		   	updateViews.setOnClickPendingIntent(R.id.widgetRelativeLayout, pendingIntent);
		   	
		   	
  		   	//Let's download
		   	
		   	String symbol = mSymbol;
        	
		   	
        	//Fix for the DOW
        	if(mSymbol.compareToIgnoreCase("^DJI") == 0){
        		symbol = "INDU";
        	}
        	
        	
        	
  		   	String url = "http://finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=l1c1p2n";
  		   	String result = URLFetcher.getString(url);
  		   	
    	
  		  SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
	   		 SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		   	 Date date = new Date();
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
			    	 if(mSymbol.endsWith("=X"))
			    		 updateViews.setTextViewText(R.id.widgetStockSymbol, companyName);
			    	 else
			    		 updateViews.setTextViewText(R.id.widgetStockSymbol, mSymbol);
   	
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
			    	
			    	
			   		// Push update for this widget to the home screen
		  		   	//appWidgetManager.updateAppWidget(mAppWidgetId, updateViews);
		  		   	
		  		   	
		  		   	// Push update for this widget to the home screen
		  		     //ComponentName thisWidget = new ComponentName(v.getContext(), Widget.class);
		  		     AppWidgetManager manager = AppWidgetManager.getInstance(v.getContext());
		  		     manager.updateAppWidget(mAppWidgetId, updateViews);

		  		     //manager.updateAppWidget(thisWidget, updateViews);
		  		     
		  		     
		  		     //Let's store the WidgetID in a preferences file
		  		   SharedPreferences prefs = getApplicationContext().getSharedPreferences("widget_preferences", 0);
		  		   Editor editor = prefs.edit();
		  		   editor.putInt(mSymbol, mAppWidgetId);
		  		   editor.commit();
		  		   
		  		   //Lets' set the Widget Alarm
		  		   AlarmManager mgr=(AlarmManager)v.getContext().getSystemService(Context.ALARM_SERVICE);
		  		   Intent alarmIntent=new Intent(v.getContext(), OnWidgetAlarmReceiver.class);
		  		   PendingIntent pi=PendingIntent.getBroadcast(v.getContext(), 0,alarmIntent, 0);
		  		   mgr.setRepeating(AlarmManager.RTC,SystemClock.elapsedRealtime()+10000,Markets.PERIOD,pi);
		  		   
		  		   //Log.d(getLocalClassName(),"Widget Alarm Set / Reset");
		    	 
	    		}catch(Exception e){
	    		}
	    	}
  		   	
	  		  Intent resultValue = new Intent();
	  		  resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
	  		  setResult(RESULT_OK, resultValue);
	  		  finish();


           	
           }
       });
       
       
       mTracker.dispatch();
     
    }
    
   


    
    @Override
    protected void onSaveInstanceState(Bundle outState) {      
        Log.d(this.getLocalClassName(), "onSaveInstanceState()");
        
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onPause() {
        Log.d(this.getLocalClassName(), "onPause()");

        super.onPause();
    }
    
    @Override
    protected void onStop() {      
        Log.d(this.getLocalClassName(), "onStop()");
        
        if(mDb.getDatabase().isOpen())
        	mDb.close();
  
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
        Log.d(this.getLocalClassName(), "onDestroy()");
        
        if(mDb.getDatabase().isOpen())
        	mDb.close();
        
        mTracker.stop();
        
        super.onDestroy();
    }
    
    @Override
    protected void onResume() {
    	Log.d(this.getLocalClassName(), "onResume()");
    	
    	if(!mDb.getDatabase().isOpen())
    		mDb.open();
    	
    	super.onResume();
    }
    
    @Override
    protected void onRestart() {        
    	Log.d(this.getLocalClassName(), "onRestart()");
    	
    	if(!mDb.getDatabase().isOpen())
    		mDb.open();
      
        super.onRestart();
    }
    
    
    protected void onStart() {
    	Log.d(this.getLocalClassName(), "onStart()");
	
        super.onStart();  
}
   	
 
}