package com.pocketools.stockalert;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.ads.*;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.pocketools.stockalert.R;
import com.pocketools.stockalert.service.OnAlarmReceiver;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class StockAlert extends ListActivity implements View.OnCreateContextMenuListener{
	
	private static final int HELLO_ID = 1000;
	private static final int MENU_REFRESH = 1001;
	private static final int MENU_SEARCH_COMPANY = 1002;
	private static final int MENU_SEARCH_CURRENCY = 1003;
	private static final int MENU_INFO = 1004;
	private static final int MENU_SETTINGS = 1005;
	private static final int MENU_ADS = 1010;
    public static final int RELOAD_CURRENCY_LIST = 1006;
    public static final int RELOAD_CURRENCY_LIST_SUCCESS = 1007;
    public static final int SHOW_MOVE_UP_DOWN_RESPONSE = 1008;
    public static final int SHOW_MOVE_UP_DOWN = 1009;  
    public static final String ANALYTICS_UA_NUMBER = "UA-12361531-3";
    												 

    
    
    
	Intent mIntent;
    private DBAdapter mDb;
    ArrayList<String> mCurrencyList;
    //View mFooterView;
    //String mDefaultCurrency;
    String mSymbol;
    String mCompanyName;
    String mAlertMessage;
   // double mDefaultCurrencyVersusEuro;
    //String mDefaultAmount;
    TextView mHeadingView;
    TextView mXrateUpdateMessage;
    String mRefreshDateText = "";
    DownloadXRatesTask mXratesTask;
    private NotificationManager mNotificationManager;
    GoogleAnalyticsTracker mTracker;
    String mCurrencyString;
    //AdView mAdView;
    boolean mIsCurrentlyUpdating = false;
    ListView mListView;
    Cursor mStockList;
    String mLastUpdate;
    boolean mForceRefresh = false;
    Context mContext = null;
    private String mDefaultLocation = "Current Location";
    private String mAdPreferences = "";
    
    
 // Replace with your own AdSense client ID.
    private static final String CLIENT_ID = "ca-mb-app-pub-1805290976571198";

    // Replace with your own company name.
    private static final String COMPANY_NAME = "DELADS";

    // Replace with your own application name.
    private static final String APP_NAME = "Stock Alert";

    // Replace with your own keywords used to target Google ad.
    // Join multiple words in a phrase with '+' and join multiple phrases with ','.
    private String mKeywords = "stock market trading";

    // Replace with your own AdSense channel ID.
    private static final String CHANNEL_ID = "7142473055";
    
    
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);  
        
        mTracker = GoogleAnalyticsTracker.getInstance();        
        // Start the tracker in manual dispatch mode...
        mTracker.startNewSession(ANALYTICS_UA_NUMBER, this);
        mTracker.trackPageView("/StockAlert");
        
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);       
        setContentView(R.layout.list_header);
        
        mContext = this;
           
           
        mDb = new DBAdapter(this);
        mDb.open();
        mDb.getDatabase().setLockingEnabled(true);
        
        boolean hasDefaultLocationSet = false;
        boolean hasAdPreferencesSet = false;
	
		//If we are forcing a refresh, let's set it here
		mForceRefresh = this.getIntent().getBooleanExtra("FORCE_REFRESH", false);
		
        
        Cursor defaults = mDb.getDefaultValues();
        startManagingCursor(defaults);
        
        for(int i=0; i< defaults.getCount(); i++){
        	defaults.moveToPosition(i);
        	
        	String default_type = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_TYPE)); 

        	if(default_type.compareTo("refresh_date") == 0){
        		
        		try{
        		mLastUpdate = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		
        		/*
        		if(mLastUpdate.compareTo("0") ==0){
        			mNeverReceivedAnUpdate = true;
        		}
        		*/
        		
        		mRefreshDateText = getUpdateInterval(mLastUpdate);
        		}catch(NumberFormatException e){}
    		
        		
        	} 
        	
        	else if(default_type.compareTo("default_location") ==0){
        		mDefaultLocation = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		hasDefaultLocationSet = true;
        	}
        	
        	else if(default_type.compareTo("ad_preferences") ==0){
        		mAdPreferences = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		hasAdPreferencesSet = true;
        	}
        	
        	
        	/*
        	else if(default_type.compareTo("google_keywords") ==0){
        		mKeywords = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	}
        	*/	
        	
        }
        
        
	   	 if(!hasDefaultLocationSet){
	      	mDb.addDefaultLocation(mDefaultLocation);
	      }
	 	 
	 	 if(!hasAdPreferencesSet){
	      	mDb.addAdPreferences(mAdPreferences);
	      }

        
        Gallery g = (Gallery) findViewById(R.id.gallery_portfolio);
        g.setAdapter(new ImageAdapter(this));
        
        g.setSelection(3);
        g.setSpacing(0);
        
        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
            	
            	((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(30);
            	
            	if(position == 0){
            		Intent intent = new Intent (v.getContext(),Currencies.class);
            		intent.putExtra("STILL_UPDATING", mIsCurrentlyUpdating);
        			startActivity(intent);  
        			finish();
            	}
            	
            	if(position == 1){
            		Intent intent = new Intent (v.getContext(),Commodities.class);
        			startActivity(intent);  
        			finish();
            	}
            	
            	if(position == 2){
            		Intent intent = new Intent (v.getContext(),Markets.class);
            		intent.putExtra("STILL_UPDATING", mIsCurrentlyUpdating);
            		intent.putExtra(Markets.IS_COMING_FROM_NAVIGATION, true);
        			startActivity(intent);  
        			finish();
            	}
            	
            	if(position == 4){
            		Intent intent = new Intent (v.getContext(),AlertList.class);
            		intent.putExtra("STILL_UPDATING", mIsCurrentlyUpdating);
        			startActivity(intent);  
        			finish();
            	}
            }
        });
        
        
    	mStockList = mDb.getStocks();      
        startManagingCursor(mStockList);
        
        ListAdapter listAdapter = new MyCurrencyAdapter(this,mStockList);       
        mListView = getListView();
       	        		
        
        mListView.setOnCreateContextMenuListener(this);
        
            ((TouchInterceptor) mListView).setDropListener(mDropListener);
            ((TouchInterceptor) mListView).setRemoveListener(mRemoveListener);
            mListView.setCacheColorHint(0);
        
            mListView.setAdapter(listAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() { 
        	
        	public void onItemClick(AdapterView<?> parentAdapterView, View view, int pos, long id_long) { 
        		
        		((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(30);

                mStockList = mDb.getStocks(); 
                startManagingCursor(mStockList);

        		view.setBackgroundColor(Color.parseColor("#FF9900"));
        		Intent intent = new Intent(view.getContext(),StockDetail.class);	
        		
        		mStockList.moveToPosition(pos);
        		String symbol = mStockList.getString(mStockList.getColumnIndex(DBAdapter.KEY_SYMBOL));
        		String companyName = mStockList.getString(mStockList.getColumnIndex(DBAdapter.KEY_COMPANY_NAME));
        		String exchange = mStockList.getString(mStockList.getColumnIndex(DBAdapter.KEY_STOCK_EXCHANGE));
        		
        		intent.putExtra("SYMBOL", symbol);
        		intent.putExtra("COMPANY_NAME_START",companyName);
        		intent.putExtra("STOCK_EXCHANGE",exchange);
        		intent.putExtra("STILL_UPDATING", true);
        		
        		
        		//Let's shutdown the background sync thread to avoid issues with
        		//updating the DB in StockDetail
        		
        		if(mNotificationManager != null)
                	mNotificationManager.cancel(HELLO_ID);
                
                if(mXratesTask != null && mXratesTask.getStatus() == AsyncTask.Status.RUNNING)
                	mXratesTask.cancel(true);

        		
        		startActivity(intent);
        	} 
        	
        }); 
        
        mListView.setOnItemLongClickListener(new OnItemLongClickListener(){

            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
           
                ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(30);
                

            	mStockList = mDb.getStocks(); 
        		v.setBackgroundColor(Color.parseColor("#FF9900"));
        		
        		mStockList.moveToPosition(pos);
        		String symbol = mStockList.getString(mStockList.getColumnIndex(DBAdapter.KEY_SYMBOL));
        		String companyName = mStockList.getString(mStockList.getColumnIndex(DBAdapter.KEY_COMPANY_NAME));
        		      		
          		
          		Intent intent = new Intent(v.getContext(),StockAlertDialog.class);	
        		
          		intent.putExtra("SYMBOL", symbol);
         		intent.putExtra("COMPANY_NAME_START",companyName);
         		
	        		startActivityForResult(intent, SHOW_MOVE_UP_DOWN_RESPONSE);
        		
                    return false;
            }
        });
    
        
        mXrateUpdateMessage = (TextView)findViewById(R.id.xrate_list_update_message);
        mXrateUpdateMessage.setText(mRefreshDateText);

        ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = serviceConn.getActiveNetworkInfo();
	    
	    if(networkInfo != null){
	    	
	    	//We only force an update if the update interval is longer than 15 mins
	    	//This will happen at the very start update also.
	    	
	    	if(isOverdueRefresh() && mIsCurrentlyUpdating == false){
	    		mIsCurrentlyUpdating = true;
		    	downloadRates();
	    	}
	    	
	    	
        }
	    else{
	    	Toast.makeText(this, "Connection Error. Please check your connections and try again.", Toast.LENGTH_SHORT).show();	 
	    }
	    
	    try{
	    
	    	/*
		 // Set up GoogleAdView.
	        GoogleAdView adView = (GoogleAdView) findViewById(R.id.adview_portfolio_list);
	        AdSenseSpec adSenseSpec =
	            new AdSenseSpec(CLIENT_ID)     // Specify client ID. (Required)
	            .setCompanyName(COMPANY_NAME)  // Set company name. (Required)
	            .setAppName(APP_NAME)          // Set application name. (Required)
	            .setKeywords(mKeywords)         // Specify keywords.
	            .setChannel(CHANNEL_ID)        // Set channel ID.
	            .setAdType(AdType.TEXT)        // Set ad type to Text.
	            .setAdTestEnabled(false);       // Keep true while testing.
	        
	        adView.showAds(adSenseSpec);
	        */
	    	
	    	
	    	/*
	    	// Required Google AdSense network parameters 
        	AdWhirlAdapter.setGoogleAdSenseCompanyName(COMPANY_NAME); 
        	AdWhirlAdapter.setGoogleAdSenseAppName(APP_NAME); 
        	 
        	// Optional Google AdSense network parameters 
        	AdWhirlAdapter.setGoogleAdSenseChannel(CHANNEL_ID); 
        	AdWhirlAdapter.setGoogleAdSenseExpandDirection("TOP"); 

        	AdWhirlTargeting.setKeywords(mKeywords);

        	
        	TableLayout layout = (TableLayout) findViewById(R.id.portfolio_list_table_layout); 
        	//AdWhirlLayout adWhirlLayout = new AdWhirlLayout(this, "db0a5aff9397431ebb02e8d023a607cb"); 
        	AdWhirlLayout adWhirlLayout = new AdWhirlLayout(this, "3b5a5a364eb14d6c80d2fade1372844e"); 
        	adWhirlLayout.setBackgroundColor(Color.BLACK);

        	
        	final int DIP_WIDTH = 320; 
        	final int DIP_HEIGHT = 52; 
        	final float DENSITY = getResources().getDisplayMetrics().density; 
        	int scaledWidth = (int) (DENSITY * DIP_WIDTH + 0.5f); 
        	int scaledHeight = (int) (DENSITY * DIP_HEIGHT + 0.5f); 
        	
        	RelativeLayout.LayoutParams adWhirlLayoutParams = 
        	    new RelativeLayout.LayoutParams(scaledWidth, scaledHeight); 
        	
        	layout.addView(adWhirlLayout, adWhirlLayoutParams); 
        	layout.invalidate(); 
        	*/
        
	    }catch(Exception e){
   		 Log.e(getLocalClassName(),"Error Retriving Google Ad");
   	 }
	    
	    
	    
	    String urlExtension = "";
        //Let's get our daily deals
        if(mDefaultLocation.compareTo("Current Location") == 0){
        	
          
          LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	      Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	      
	      if(location != null){
		      String latitude = Double.toString(location.getLatitude());
		      String encodedLat = latitude.replace(".", "%2E");
		      
		      String longitude = Double.toString(location.getLongitude());
		      String encodedLong = longitude.replace(".", "%2E");
		      
		      urlExtension = "&lat=" + encodedLat + "&lng=" + encodedLong + "&radius=10";
	      }
	      else
	    	  urlExtension = "&division_id=" + mDefaultLocation;

        	
        }
        else
        	urlExtension = "&division_id=" + mDefaultLocation;
	    
	    DownloadDeals deals = new DownloadDeals();
    	deals.execute("http://api.groupon.com/v2/deals.json?client_id=" + Markets.GROUPON_CLIENT_ID + urlExtension);
       
    
        
        mTracker.dispatch();
       
        
    }
    
    private TouchInterceptor.RemoveListener mRemoveListener =
        new TouchInterceptor.RemoveListener() {
        public void remove(int which) {
            //removePlaylistItem(which);
        }
    };
    
    
    private class DownloadDeals extends AsyncTask {
		
	     public Object doInBackground(Object... urls) { 
	    	 
	    	 /*
	    	 String locations = URLFetcher.getString("https://api.groupon.com/v2/divisions.json?client_id=1b275b4fea756d0e9cd2ebb4f2fba9a42df320d3");
	    	 
	    	 try{
	    	 JSONObject object = new JSONObject(locations);
	    	 JSONArray array = object.getJSONArray("divisions");
	    	 
	    	 for(int i=0;i<array.length();i++){
	    		 JSONObject obj = array.getJSONObject(i);
   			 String divisionName = obj.getString("name");
   			 Log.d("Stock Alert",divisionName + ",");
	    		 
	    	 }
	    	 
	    	 }catch(Exception e){}
	    	 */
	    	 
	    	 
	    	 
	    	 return URLFetcher.getString((String)urls[0]);    	    	 
	     }

	     public void onPostExecute(Object result) {
	    	 
	    	 
   		LinearLayout horizontal = (LinearLayout)findViewById(R.id.horizontal_layout);
	        horizontal.setGravity(Gravity.CENTER);
	        
	        
	        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
	       	     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

	       	layoutParams.setMargins(0, 3, 2, 2);
	    	 
	    	 if(result == null){
	    		 TextView ad = new TextView(mContext);
	    	        ad.setBackgroundColor(Color.parseColor("#b2840a"));
	    	        ad.setWidth(360);
	    	        ad.setText("0 daily deals for your current location.   \nClick here to manage Ads Preferences");
	    	        ad.setTextSize(12);
	    	        ad.setPadding(5, 0, 5, 0);
	    	        ad.setTypeface(null,Typeface.BOLD);
	    	        ad.setTextColor(Color.WHITE);
	    	        horizontal.addView(ad,layoutParams);
	    	        
	    	        ad.setOnClickListener(new View.OnClickListener() {
	    	            public void onClick(View v) {
	    	            	
	    	            	mTracker.trackEvent(
	    	                        "Ads Preferences Manager",  		// Category
	    	                        "From Ad",  			// Action
	    	                        "", // Label
	    	                        0);       		// Value (Don't know what this means)
	    	                  
	    	            	mTracker.dispatch();
	    	            	
	    	            	Intent intent = new Intent (v.getContext(),AdsPreferencesManager.class);
	    	    			startActivity(intent); 
	    	    			
	    	    			finish();        		
	    	            }
	    	        });		
	    	 }
	         
	    	 else{	
	    		 

	    	       	
	    	       	LinkedList<Deal> dealList = new LinkedList<Deal>();
	    	       	//Log.d("Stock Alert","About to read the JSON object ");
	    		 
	    		 try{
		    		 JSONObject json = new JSONObject((String)result);
		    		 JSONArray array = json.getJSONArray("deals");
		    		 
		    		 String divisionName = "";
		    		 int dealVolume = array.length();
		    		 
	    		 
		    		 boolean headDivisionSet = false;
		    		 String headDivision = "";
	    		 
		    		 for(int i=0; i<array.length();i++){
		    			 JSONObject dealObject = array.getJSONObject(i);
		    			 JSONObject division = dealObject.getJSONObject("division");
		    			 divisionName = division.getString("name");
		    			 
			    		 //Let's set the head division with the FIRST Deal not the last
		    			 if(!headDivisionSet){
		    				 headDivision = divisionName;
		    				 headDivisionSet = true; 
		    			 }
		    			 
		    			 
		    			 JSONArray tags = dealObject.getJSONArray("tags");
		    			 
		    			 if(tags.length() > 0){
		    				 
		    				 boolean match = false;
		    				 String adTypeString = "";
		    				 
		    				 for(int j=0;j<tags.length();j++){
		    					 JSONObject adType = tags.getJSONObject(j);
				    			 adTypeString = adType.getString("name"); 
				    			 
				    			// Log.d("Stock Alert","AdType = " + adTypeString);
				    			// Log.d("Stock Alert","Checking with " + mAdPreferences);
				    			 
				    			 
				    			 if(mAdPreferences.contains(adTypeString)){
				    				 match = true;
				    			 }
		    				 }	
		    				 
		    				//If we don't have a match on the tags, then we can add
		    				 if(!match){
		    					 Deal deal = new Deal();
				    			 deal.title = dealObject.getString("announcementTitle");
				    			 deal.url = dealObject.getString("dealUrl");
				    			 deal.division = divisionName;
				    			 deal.type = adTypeString;
				    			 
				    			 deal.isNowDeal = dealObject.getBoolean("isNowDeal");
				    			 
				    			// Log.d("Stock Alert","Now! Deal = " + dealObject.getString("isNowDeal"));
				    			 
				    			 dealList.add(deal);
		    				 }
			    			 
		    			 }

		    			 else{ //Let's just go ahead and add it if we don't have tags
		    				 Deal deal = new Deal();
			    			 deal.title = dealObject.getString("announcementTitle");
			    			 deal.url = dealObject.getString("dealUrl");			    			 
			    			 dealList.add(deal);
		    			 }
		    		 }	
		    		 
		    		 
		    		// Log.d("Stock Alert","Contains " + dealList.size() + "Number of deals");
		    		 
		    		 
		    		 
		    		 
		    		 
		    		 if(dealList.size() > 0){
		    			 
		    			 int numberOfDeals = dealList.size();
		    			// Log.d("Stock Alert","Contains " + numberOfDeals + "Number of deals");

		    			 TextView ad = new TextView(mContext);
			    	        ad.setBackgroundColor(Color.parseColor("#b2840a"));
			    	        ad.setWidth(200);
			    	        ad.setText(numberOfDeals + " (of " + dealVolume + ")" + " daily deals for " + headDivision + ".   Click here to manage Ads Preferences");
			    	        ad.setTextSize(12);
			    	        ad.setPadding(5, 0, 5, 0);
			    	        ad.setTypeface(null,Typeface.BOLD);
			    	        ad.setTextColor(Color.WHITE);
			    	        horizontal.addView(ad,layoutParams);
			    	        
			    	        ad.setOnClickListener(new View.OnClickListener() {
			    	            public void onClick(View v) {
			    	            	
			    	            	mTracker.trackEvent(
			    	                        "Ads Preferences Manager",  		// Category
			    	                        "From Ad",  			// Action
			    	                        "", // Label
			    	                        0);       		// Value (Don't know what this means)
			    	                  
			    	            	mTracker.dispatch();
			    	            	
			    	            	Intent intent = new Intent (v.getContext(),AdsPreferencesManager.class);
			    	    			startActivity(intent); 
			    	    			
			    	    			finish();        		
			    	            }
			    	        });		 
		    		 }
		    		 else{
		    			 TextView ad = new TextView(mContext);
			    	        ad.setBackgroundColor(Color.WHITE);
			    	        ad.setWidth(200);
			    	        ad.setText("0 daily deals for your current location.   Click here to manage Ads Preferences");
			    	        ad.setTextSize(12);
			    	        ad.setPadding(5, 0, 5, 0);
			    	        ad.setTypeface(null,Typeface.BOLD);
			    	        ad.setTextColor(Color.BLACK);
			    	        horizontal.addView(ad,layoutParams);
			    	        
			    	        ad.setOnClickListener(new View.OnClickListener() {
			    	        	
			    	            public void onClick(View v) {
			    	            	
			    	            	
			    	            	mTracker.trackEvent(
			    	                        "Ads Preferences Manager",  		// Category
			    	                        "From Ad",  			// Action
			    	                        "", // Label
			    	                        0);       		// Value (Don't know what this means)
			    	                  
			    	            	mTracker.dispatch();
			    	            	
			    	            	Intent intent = new Intent (v.getContext(),AdsPreferencesManager.class);
			    	    			startActivity(intent); 
			    	    			
			    	    			finish();        		
			    	            }
			    	        });		
		    		 }
		    		 
		    		 Iterator<Deal> iter = dealList.iterator();
		    		 while(iter.hasNext()){
		    			 
		    			 Deal deal = (Deal)iter.next();
		    			 TextView ad = new TextView(mContext);
		    			 
		    			 if(deal.isNowDeal())
		    				 ad.setBackgroundColor(Color.YELLOW);
		    			 
		    			 else
		    				 ad.setBackgroundColor(Color.WHITE);
		    			 
		    	         ad.setWidth(200);
		    	         
		    	         StringBuffer title = new StringBuffer(deal.title);
		    	         
		    	         if(deal.isNowDeal())
		    	        	 title.append(" (Now! Deal)");
		    	         
		    	         ad.setText(title.toString());
		    	         ad.setTextSize(12);
		    	         ad.setPadding(5, 0, 5, 0);
		    	         ad.setTypeface(null,Typeface.BOLD);
		    	         ad.setTextColor(Color.BLACK);
		    	         horizontal.addView(ad,layoutParams);
		    	        
		    	        //String dealURL = deal.url;
		    	         ad.setTag(deal);
		    	        
		    	         ad.setOnClickListener(new View.OnClickListener() {
		    	            public void onClick(View v) {
		    	            	
		    	            	Deal deal = (Deal)v.getTag();
		    	            	
		    	            	mTracker.trackEvent(
		    	                        "Ad Click",  		// Category
		    	                        deal.division,  			// Action
		    	                        deal.type, // Label
		    	                        0);       		// Value (Don't know what this means)
		    	                  
		    	            	mTracker.dispatch();
		    	            	
		    	            	//Log.d("Stock Alert",Markets.CJ_AFFILIATE + deal.url);

		    	            	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Markets.CJ_AFFILIATE + deal.url)));          		
		    	            }
		    	         });
		    			 
		    			 
		    		 }		    		 
	    		 }
	    		 catch (Exception e){
	    			 Log.e("Stock Alert", e.getMessage());
	    		 }

	    	 }
	     }
	    		 
	
	 }
    
    
    
    private TouchInterceptor.DropListener mDropListener =
        new TouchInterceptor.DropListener() {
        public void drop(int from, int to) {
            
            Cursor mTrackCursor = mDb.getStocks();
            startManagingCursor(mTrackCursor);       

        	mTrackCursor.moveToPosition(from);
        	int from_id = mTrackCursor.getInt(0);
       	
        	mTrackCursor.moveToPosition(to);
        	int to_id = mTrackCursor.getInt(0);
        	
        	Random generator = new Random();
        	int random = generator.nextInt() + 1000000;
            
            if (from < to) {
            	
            	mDb.setStockId(random, from_id);           	
            	for (int i = from + 1; i <= to; i++) {
                    mTrackCursor.moveToPosition(i);
                    int oldId = mTrackCursor.getInt(0);
                    int newId = oldId - 1;

                    mDb.setStockId(newId, oldId);
                    
                }

            	mDb.setStockId(to_id, random);
            	           	
               
            } else if (from > to) {
            	
            	//Because _id is a primary number, it is unique
            	//We need to provide a placeholder for the from position
            	mDb.setStockId(random, from_id);           	
                for (int i = from - 1; i >= to; i--) {
                	
                    mTrackCursor.moveToPosition(i);
                    int oldId = mTrackCursor.getInt(0);
                                       
                    int newId = oldId + 1;

                    mDb.setStockId(newId, oldId);
                    
                }
                mDb.setStockId(to_id, random);
            }
            
            setCurrencyList();
        }
    };
    
    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;

        private Integer[] mImageIds = {
        		R.drawable.currencies_off,
        		R.drawable.commodities_off,
        		R.drawable.markets_off,
                R.drawable.portfolio_on,
                R.drawable.alerts_off
        };

        
        public ImageAdapter(Context c) {
            mContext = c;
            
            /*
            TypedArray a = obtainStyledAttributes(R.styleable.Theme);
            mGalleryItemBackground = a.getResourceId(
                    R.styleable.Theme_android_galleryItemBackground, 0);
            a.recycle();
            */
            
        }
        

        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);

            i.setImageResource(mImageIds[position]);
          //  i.setLayoutParams(new Gallery.LayoutParams(150, 40));
           // i.setScaleType(ImageView.ScaleType.FIT_XY);
           // i.setBackgroundResource(mGalleryItemBackground);

            return i;
        }
    }
    
    
    
    
    
    /**
     * Indicates whether the specified service is already started.This
     * method queries the activity manager for launched services that can
     * respond to an binding with an specific service name.
     * If no existed service is found, this method returns null.
     *
     * @param context The context of the activity
     * @param className The service full name to check for availability.
     *
     * @return ComponentName if the service is already existed, NULL otherwise.
     */
    public static ComponentName isServiceExisted(Context context,String className)
    {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

        if(!(serviceList.size() > 0)){
                return null;
        }

        for(int i = 0; i < serviceList.size(); i++){
                RunningServiceInfo serviceInfo = serviceList.get(i);
                ComponentName serviceName = serviceInfo.service;

                if(serviceName.getClassName().equals(className))
                {
                        return serviceName;
                }
        }

        return null;
    }

    
    private void setCurrencyList(){
    	Cursor c = mDb.getStocks();      
        startManagingCursor(c);
        
        ListAdapter listAdapter = new MyCurrencyAdapter(this,c);       
        mListView = getListView();
        mListView.setAdapter(listAdapter);
    }
    
    

    
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
	    	
	 	
	    	menu.add(0,MENU_SEARCH_COMPANY,0,"Add Stock")
	 		.setIcon(android.R.drawable.ic_menu_add);
	    	
	    	menu.add(0,MENU_ADS,0,"Ads Preferences Manager")
	 		.setIcon(android.R.drawable.ic_menu_edit);
	    	
	    	menu.add(0,MENU_SEARCH_CURRENCY,0,"Add Currency")
	 		.setIcon(android.R.drawable.ic_menu_add);
	    	
	    	menu.add(0,MENU_REFRESH,0,"Refresh Rates")
	 		.setIcon(android.R.drawable.ic_menu_rotate);
	    	
	    	menu.add(0,MENU_INFO,0,"Info")
	 		.setIcon(android.R.drawable.ic_menu_help);
	    	
	    	menu.add(0,MENU_SETTINGS,0,"Settings")
	 		.setIcon(android.R.drawable.ic_menu_edit);
	    	
    	  return result;

    }
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {   
        
        
	    case MENU_SEARCH_COMPANY:
	    	
	    	Intent intent = new Intent (this,AddStock.class);
			startActivity(intent); 
			
			finish();
			return true; 
			
	    case MENU_ADS:
	    	
	    	mTracker.trackEvent(
                    "Ads Preferences Manager",  		// Category
                    "From Menu",  			// Action
                    "", // Label
                    0);       		// Value (Don't know what this means)
              
        	mTracker.dispatch();
        	
	    	intent = new Intent (this,AdsPreferencesManager.class);
			startActivity(intent); 
			
			finish();
			return true; 
			
	    case MENU_SEARCH_CURRENCY:
	    	
	    	intent = new Intent (this,AddCurrency.class);
			startActivity(intent); 
			
			finish();
			return true; 
			
	    	case MENU_REFRESH:      
        	
        	//Let's check the network status once more
        	ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    	    NetworkInfo networkInfo = serviceConn.getActiveNetworkInfo();
    	    
    	    if(networkInfo != null){
    	    	mIsCurrentlyUpdating = true;
    	    	
    	    	setCurrencyList();
    	    	downloadRates();
    	    }
    	    else{
    	    	
    	    	//setTitle(mSyncMessage + mRefreshDateText);
    	    	mXrateUpdateMessage.setText(mRefreshDateText);
    	    	Toast.makeText(this, "Connection Error. Please check your connections and try again.", Toast.LENGTH_SHORT).show();
            }	
        	return true;
			
	    case MENU_INFO:
	    	
	    	intent = new Intent (this,Info.class);
			startActivity(intent); 
			
			//finish();
			return true; 
			
	    case MENU_SETTINGS:
	    	
	    	intent = new Intent (this,Settings.class);
			startActivity(intent); 
			finish();
			
			return true;        
    	
		
    }
        
        
        
        return false;
    }
    
    
    //Let's override the default search functionality to call up our Stock search instead
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
  	  if (keyCode == KeyEvent.KEYCODE_SEARCH) { 
  		  
  		Intent intent = new Intent (this,AddStock.class);
		startActivity(intent); 		
		finish();     
        return true;
  	  } 
  	  
  	if (keyCode == KeyEvent.KEYCODE_BACK) { 
		finish();     
        return true;
  	  } 
  	  
  	  return false;
  	} 
    
    
    
    private void downloadRates(){
    	
    	Cursor currencies = mDb.getStocks();
        startManagingCursor(currencies);
        
        StringBuffer currencylist = new StringBuffer();
        int stockCount = 0;
        
        for(int i=0; i < currencies.getCount(); i++){
        	currencies.moveToPosition(i);
        	String symbol = currencies.getString(currencies.getColumnIndex(DBAdapter.KEY_SYMBOL));
        	
        	//Fix for DOW
        	//Bug fix for DOW Jones
	   		 if(symbol.compareToIgnoreCase("^DJI") ==0)
	   			 symbol = "INDU";
        	
        	currencylist.append("+" + symbol);
        	stockCount ++;
        }
        
        if(stockCount > 0){
        	
        	mXrateUpdateMessage.setText("Updating Prices ...");
        	
	        String temp = currencylist.toString();
	        //String trimmedList = temp.replaceFirst("+", "");
	        
	        mCurrencyString = "http://finance.yahoo.com/d/quotes.csv?s=" + temp + "&f=sxl1c1p2t1d1hgkjj1ab2bb3pot8va2ren";
	        																	
	        
	        startRefreshNotification();
	        mXratesTask = new DownloadXRatesTask();
	        mXratesTask.execute(mCurrencyString);
        }
    	
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
    	switch (requestCode) {
    	
    		case RELOAD_CURRENCY_LIST:
	    		
	    		if(resultCode == RELOAD_CURRENCY_LIST_SUCCESS){
	    			
	    			if(!mDb.getDatabase().isOpen())
	    	    		mDb.open();
	    			
	    			Cursor c = mDb.getStocks();      
	    	        startManagingCursor(c);
	    	        
	    	        this.setListAdapter(new MyCurrencyAdapter(this,c));
	    	        
	    		}	
	    		
    		case SHOW_MOVE_UP_DOWN_RESPONSE:
	    		
    			if(resultCode == SHOW_MOVE_UP_DOWN)
    				showDialog(SHOW_MOVE_UP_DOWN);
                
              default:
            	  break;
        }
    }
    
    
    private class DownloadXRatesTask extends AsyncTask<String, Integer, String>{
    	
    	public DBAdapter mThreadDB;
    	public Cursor mCursor;
		
	     public String doInBackground(String... urls) {	 
	    	 
	    	// Log.d(getLocalClassName(),"DownloadXRatesTask Called");
	 
	    	 String result = URLFetcher.getString((String)urls[0]);

	    	 if(result != null){	
	    		     		 
	    		 if(!mThreadDB.getDatabase().isOpen())
	    			 mThreadDB.open();

	    		 
		    	 String resultString = (String)result;
		    	 
	    		 //Initialise the date String. We will come across this first in the xml file
	    		 //so no worries here
		    	 //String date = mRefreshDateText;

	    		 //In order of csv
	    		 //Company name goes at the end due to some companies having a "," in the name
	    		 String symbol = ""; //s
	    		 String stockExchange = ""; //x
	    		 String lastTrade = ""; //l1
	    		 String changeInPrice = ""; //c1
	    		 String changeInPercentage = ""; //p2
	    		 String lastTradeTime = ""; //t1
	    		 String lastTradeDate = ""; //d1		    		 
	    		 String dailyHigh = ""; //h
	    		 String dailyLow = "";//g
	    		 String yearlyHigh = ""; //k
	    		 String yearlyLow = ""; //j
	    		 String fiftyTwoWeekRange = ""; //w
	    		 String marketCap = ""; //j1 		 
	    		 String askPrice = ""; //a
	    		 String askPriceRealtime = ""; //b2
	    		 String bidPrice = ""; //b
	    		 String bidPriceRealtime = ""; //b3
	    		 String previousClosePrice = ""; //p
	    		 String openPrice = ""; //o
	    		 String one_year_target_price = ""; //t8
	    		 String volume_average_3m = ""; //v
	    		 String volume_average_daily = ""; //a2
	    		 String priceEarningsRatio = ""; //r
	    		 String earningsPerShare = ""; //e
	    		 String companyName = ""; //n
		    	 
		    	 StringTokenizer tokenizer = new StringTokenizer(resultString, "\n");
		    	 while(tokenizer.hasMoreTokens()){
		    		 String token = tokenizer.nextToken();	
		    		 
		    		// Log.d(getLocalClassName(),token);
		    		 
		    		 StringTokenizer subtokenizer = new StringTokenizer(token,",");
		    		 
		    		 
		    		 HashMap<String, String>stockMap = new HashMap<String, String>();
		    		 
		    		 symbol = subtokenizer.nextToken().replaceAll("\"", "");	
	    		 
		    		//Bug fix for DOW Jones
		    		 if(symbol.compareToIgnoreCase("INDU") ==0)
		    			 symbol = "^DJI";
		    		 
		    		 stockMap.put("symbol", symbol);
		    		 
		    		 stockExchange = subtokenizer.nextToken().replaceAll("\"", "");
		    		 
		    		 //bug fix for NASDAQ stock exchange
		    		 if(stockExchange.startsWith("Nasdaq"))
		    			 stockExchange = "Nasdaq";
		    		 
		    		 stockMap.put("stock_exchange", stockExchange);
		    		 

		    		 
		    		 lastTrade = subtokenizer.nextToken().replaceAll("\"", "");
		    		 stockMap.put("last_trade_price", lastTrade);
		    		 
		    		 changeInPrice = subtokenizer.nextToken().replaceAll("\"", "");
		    		 stockMap.put("change_in_price", changeInPrice);
		    		 
		    		 changeInPercentage = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("change_in_percentage", changeInPercentage);
		    		 
		    		 lastTradeTime = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 String newLastTradeTime = lastTradeTime.toUpperCase() + " EST";
		    		 stockMap.put("last_trade_time", newLastTradeTime);
		    		 
		    		 lastTradeDate = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("last_trade_date", lastTradeDate);
		    		 
		    		 dailyHigh = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("daily_high", dailyHigh);
		    		 
		    		 dailyLow = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("daily_low", dailyLow);
		    		 
		    		 //Temp bug fix for ^DJI
		    		 if(symbol.compareTo("INDU")==0){
		    			 stockMap.put("daily_high", "");
			    		 stockMap.put("daily_low", "");
		    		 }
		    		 
		    		 
		    		 yearlyHigh = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("yearly_high", yearlyHigh);
		    		 
		    		 yearlyLow = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("yearly_low", yearlyLow);
		    		 /*
		    		 fiftyTwoWeekRange = subtokenizer.nextToken().replaceAll("\"", "");
		    		 stockMap.put("fifty_two_week_range", fiftyTwoWeekRange);
		    		 Log.d("StockValue","fiftyTwoWeekRange :" + fiftyTwoWeekRange);
		    		 */
		    		 marketCap = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("market_cap", marketCap);
		    		 
		    		 askPrice = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("ask_price", askPrice);
		    		 
		    		 askPriceRealtime = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("ask_price_realtime", askPriceRealtime);
		    		 
		    		 bidPrice = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("bid_price", bidPrice);
		    		 
		    		 bidPriceRealtime = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("bid_price_realtime", bidPriceRealtime);
		    		 
		    		 previousClosePrice = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("previous_close_price", previousClosePrice);
		    		 
		    		 openPrice = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("open_price", openPrice);
	    		 
		    		 one_year_target_price = subtokenizer.nextToken().replaceAll("\"", "");
		    		 stockMap.put("one_year_target_price", one_year_target_price);
		    		 
		    		 volume_average_3m = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("volume_average_3m", volume_average_3m);
		    		 
		    		 volume_average_daily = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("volume_average_daily", volume_average_daily);
		    		 
		    		 priceEarningsRatio = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("price_earnings_ratio", priceEarningsRatio);
		    		 
		    		 earningsPerShare = subtokenizer.nextToken().replaceAll("\"", "");	
		    		 stockMap.put("earnings_per_share", earningsPerShare);
		    		 
		    		 companyName = subtokenizer.nextToken().replaceAll("\"", "");
		    		 companyName = companyName.trim();
		    		 
		    		 //Let's limit the size of the comany name so it fits nicely on the screen
		    		 int length = 13;
		    		 
		    		 if(companyName.length() > length)
		    			 companyName = companyName.substring(0, length-1);
		    		 
		    		 stockMap.put("company_name", companyName);
		    		 
		    		 
		    		 try{
		    			 mThreadDB.updateStocks(symbol, stockMap);
			    		 
			    		 //Going to change to the last time this app was updated, not last trade time
			    		 mLastUpdate = Long.toString(System.currentTimeMillis());
			    		 mThreadDB.updateRateDate(mLastUpdate);
			    		 
		    		 }catch(SQLException e){
		    			 //More than likely thrown if background thread is shut down
		    			 
		    		 }
		    		
		    		
		    		 
		    	 }
		    	 
	    	 }   
	    	 
	    	 
	    	 //Bug fix for ^DJI. We need to get this from Google as the Yahoo Finance API is too flaky
	    	 if(mSymbol.compareTo("^DJI") ==0){
	    		 
	    		 StringBuffer googleURL = new StringBuffer("http://www.google.com/finance/info?q=^DJI");
	    		 String jsonResult = URLFetcher.getString(googleURL.toString());	    		 
	    		 String trimmedResult = jsonResult.replace("// ", "");
	    		 
	    		 Log.d(getLocalClassName(), "Response from Google = " + trimmedResult);
	    		 	
	    		 
	    		 if(jsonResult != null){
	    			 try{
	    				 
	    				 JSONArray array = new JSONArray(trimmedResult);
	    				 
	    				//Let's loop through first and fetch all thumbnails
			    		 for(int i=0;i< array.length();i++){
			    			 
			    			 
			    			 JSONObject element = array.getJSONObject(i);
			    			 String symbol = element.getString("t");
			    			 Log.d(getLocalClassName(),"Symbol response from Google = " + symbol);
			    			 String lastTradePrice = element.getString("l").replace(",", "");
			    			 
			    			 String changeInPercentage = element.getString("cp") + "%";
			    			 String changeInPrice = element.getString("c");
			    			 String lastTradeTime = element.getString("ltt");
			    			 String lastTradeDate = element.getString("lt");
			    			 
			    			 String symbolTemp = symbol;
			    			 
			    			 if(symbol.startsWith(".")){
			    				 symbolTemp = symbol.replaceFirst(".", "^");
			    			 }
			    			 
			    			 
			    			 
			    			 Log.d(getLocalClassName(),"Updating realtime price for " + symbolTemp);
			    			 mThreadDB.updateRealtimePrice(symbolTemp, lastTradePrice, changeInPercentage, changeInPrice, lastTradeTime, lastTradeDate);			    			 
			    			 

			    		 }

	    			 }catch (JSONException e){
	    				 Log.d(getLocalClassName(),"Exception thrown : " + e.getMessage());
	    			 }
	    		 }	 
	    	 }
	    	 
	    	 mThreadDB.close();
	    	 
	    	 return result;
	    	 
	     }
	     
	     public void onPreExecute(){
	    	 
	    	 mThreadDB = new DBAdapter(mContext);
	    	 mThreadDB.open();
	    	 
	     }
	     
	     public void onCancelled(){
	    	 
	    	 if(mCursor != null && !mCursor.isClosed())
	    		 mCursor.close();
	    	 
	    	 if(mThreadDB.getDatabase().isOpen())
	    		 mThreadDB.close();
	    	 
	    	 mIsCurrentlyUpdating = false;
	    	 
	    	 mThreadDB = null;
	     }

	     public void onPostExecute(String result) {
	    	 
	    	 if(!mThreadDB.getDatabase().isOpen())
    			 mThreadDB.open();


   		 //Let's make sure the user is updated accordingly
	         mNotificationManager.cancel(HELLO_ID);
	         mRefreshDateText = getUpdateInterval(mLastUpdate);
	         mXrateUpdateMessage.setText(mRefreshDateText);
	         
	         mIsCurrentlyUpdating = false;

 			
 			mCursor = mThreadDB.getStocks();      
 	        startManagingCursor(mCursor);
 	        
 	        ListAdapter listAdapter = new MyCurrencyAdapter(getBaseContext(),mCursor);       
 	        ListView listView = getListView();
 	        listView.setAdapter(listAdapter);

 	        mThreadDB.close();
 	       mThreadDB = null;
	         
	     }


	 }
    
    private String getUpdateInterval(String lastUpdate){
    	
    	long lastUpdateLong = Long.parseLong(lastUpdate);
    	long currentTime = System.currentTimeMillis();
    	long intervalSecs = (currentTime - lastUpdateLong)/1000;
    	
    	String interval = "";
    	
    	if(intervalSecs < 60){
    		interval = intervalSecs + " Seconds";
    	}
    	
    	else if(intervalSecs > 60 && intervalSecs < 3600){
    		interval = (intervalSecs/60) + " Minutes";
    	}
    	
    	if(intervalSecs > 3600){
    		interval = (intervalSecs/3600) + " Hours";
    	}
 	
    	return "Last Update : " + interval ;
    }
    
    
    private boolean isOverdueRefresh(){
    	
    	if(mForceRefresh){
    		
    		//set to false so we don't keep updating after adding a stock
    		mForceRefresh=false;
    		return true;
    	}
    	else{
    	
	    	try{   	
		    	long lastUpdateLong = Long.parseLong(mLastUpdate);
		    	long currentTime = System.currentTimeMillis();
		    	long intervalSecs = (currentTime - lastUpdateLong)/1000;
		    	
		    	//If we're over 15 minutes, then refresh 
		    	if(intervalSecs > 900)
		    		return true;	    	
		    	else	
		    		return false;
		    }
	    	catch(NumberFormatException e){ return true; }
    	}
    }
    
	   private void startRefreshNotification(){
	    	
	    	// Get the notification manager serivce.
	        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	        
	        int icon = android.R.drawable.stat_notify_sync;
	        CharSequence tickerText = "Stock price sync with Yahoo Finance";
	        long when = System.currentTimeMillis();
	
	        Notification notification = new Notification(icon, tickerText, when);
	
	        
	        Context context = getApplicationContext();
	        CharSequence contentTitle = "Stock Alert";
	        CharSequence contentText = "Refreshing Stock Prices";
	        Intent notificationIntent = new Intent(this, StockAlert.class);
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	
	        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	        mNotificationManager.notify(HELLO_ID, notification);
	
	    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {      
     
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onPause() {

        if(mNotificationManager != null)
        	mNotificationManager.cancel(HELLO_ID);
        
        if(mXratesTask != null && mXratesTask.getStatus() == AsyncTask.Status.RUNNING)
        	mXratesTask.cancel(true);
        
        //mXrateUpdateMessage.setText(mRefreshDateText);
        
        if(mDb.getDatabase().isOpen())
        	mDb.close();

        super.onPause();
    }
    
    @Override
    protected void onStop() {             
        
        if(mNotificationManager != null)
        	mNotificationManager.cancel(HELLO_ID);
        
        if(mXratesTask != null && mXratesTask.getStatus() == AsyncTask.Status.RUNNING)
        	mXratesTask.cancel(true);
        
        if(mDb.getDatabase().isOpen())
        	mDb.close();
        
        super.onStop();
        
    }
    
    @Override
    protected void onDestroy() {
    	
    	if(mDb.getDatabase().isOpen())
        	mDb.close();
       
        super.onDestroy();
        mTracker.stop();
    }
    
    @Override
    protected void onResume() {
   	
    	if(!mDb.getDatabase().isOpen())
    		mDb.open();
    	
    	if(isOverdueRefresh() && mIsCurrentlyUpdating == false){
    		mIsCurrentlyUpdating = true;
    		//Let's check the network status once more
        	ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    	    NetworkInfo networkInfo = serviceConn.getActiveNetworkInfo();
    	    
    	    if(networkInfo != null)
    	    	downloadRates();
    	}
    		 	
    	setCurrencyList();
    	mXrateUpdateMessage.setText(getUpdateInterval(mLastUpdate));
    	super.onResume();
    }
    
    @Override
    protected void onRestart() {          	
    	if(!mDb.getDatabase().isOpen())
    		mDb.open();
    	
    	
    	if(isOverdueRefresh() && mIsCurrentlyUpdating == false){
    		mIsCurrentlyUpdating = true;
    		//Let's check the network status once more
        	ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    	    NetworkInfo networkInfo = serviceConn.getActiveNetworkInfo();
    	    
    	    if(networkInfo != null)
    	    	downloadRates();
    	}

    	setCurrencyList();     
        super.onRestart();
    }
    
    
    protected void onStart() {   	
    	if(!mDb.getDatabase().isOpen())
    		mDb.open();

    	super.onStart();
    }
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        
	        
        case SHOW_MOVE_UP_DOWN:
        
	        return new AlertDialog.Builder(this)
	        .setTitle(R.string.reorder_stock_title)
	        .setMessage(R.string.reorder_stock_message)
	        .setIcon(R.drawable.ic_mp_move)
	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	//finish();
	            }
	        })             
	        .create();
   
        default:
       	 return null;
        
        }
    }
   
    
    
    private class MyCurrencyAdapter extends CursorAdapter{
    	private Cursor mCursor;
    	private Context mContext;
    	private final LayoutInflater mInflater;
 
    	public MyCurrencyAdapter(Context context, Cursor cursor) {
            super(context, cursor, true);
            mInflater = LayoutInflater.from(context);
            mContext = context;
    	}
    	
    	
 
    	@Override
    	public void bindView(View view, Context context, Cursor cursor) {
    		// TODO Auto-generated method stub
    		
    			mSymbol = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_SYMBOL));
    			mCompanyName = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_COMPANY_NAME));
    			
    			String messageAlert = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_ALERT_MESSAGE));
    			
    			String companyNameStart = mCompanyName;
    			int id = cursor.getInt(0);
    			   			
    			if(mCompanyName != null){
    				
    				StringTokenizer tokenizer = new StringTokenizer(mCompanyName," ");
    				if(tokenizer.hasMoreTokens()){
    					companyNameStart = tokenizer.nextToken();
    				}
 				
    			}
    			
    			ImageView alertImage = (ImageView)view.findViewById(R.id.alert_image_random);
    			
    			if(messageAlert == null || messageAlert.startsWith("N/A")){
    				alertImage.setImageDrawable(null);
    			}
    			else{			
    				
    				alertImage.setImageDrawable(getResources().getDrawable(R.drawable.alert_image));   		
    			}
    					
    			String tickerPriceString = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_LAST_TRADE_PRICE));
    			String lastTradeDate = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_LAST_TRADE_DATE));
    			String lastTradeTime = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_LAST_TRADE_TIME));
    		
	    		TextView t = (TextView) view.findViewById(R.id.front_company_symbol);
	    		
	    		//Let's make the currencies more readable
	    		if(mSymbol.endsWith("=X"))
	    			t.setText(mCompanyName);
	    		else	    			
	    			t.setText(mSymbol);
	    		
	         	t.setTextColor(Color.WHITE);

	            
	           	t = (TextView) view.findViewById(R.id.front_stock_price);
	           	
	           	if(mIsCurrentlyUpdating)
	           		t.setText("Updating");
	           	else
	           		t.setText(tickerPriceString);
	           	
	           	
	           	
	           	t = (TextView) view.findViewById(R.id.front_last_update);
	           	
	           	if(mIsCurrentlyUpdating)
	           		t.setText("Updating");
	           	else
	           		t.setText(lastTradeTime + " : " + lastTradeDate);

    	
	            t = (TextView) view.findViewById(R.id.front_company_name);
	            t.setText(cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_COMPANY_NAME)));
	            
	           t = (TextView) view.findViewById(R.id.front_stock_change);
	     
	            String priceChange = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_CHANGE_IN_PRICE));
	            
	            if(mIsCurrentlyUpdating)
	            	t.setText("Updating");
	            else
	            	t.setText(priceChange + "  " + cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_CHANGE_IN_PERCENTAGE)));
	            
	            if(priceChange != null){
		            if(priceChange.startsWith("+")  || mIsCurrentlyUpdating)
		            	t.setTextColor(Color.GREEN);
		            else
		            	t.setTextColor(Color.RED);
	            }
            	            
    	}
    	
    	 
    	@Override
    	public View newView(Context context, Cursor cursor, ViewGroup parent) {
    		// TODO Auto-generated method stub
    		
	    		//final View view = mInflater.inflate(R.layout.list_content, parent, false);	
    			final View view = mInflater.inflate(R.layout.stock_alert, parent, false);	    		
    	    
	    		return view;
    	}
    	
    	
    	

    } 
    
}