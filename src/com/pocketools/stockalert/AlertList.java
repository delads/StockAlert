package com.pocketools.stockalert;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;


import com.google.ads.*;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.pocketools.stockalert.R;
import com.pocketools.stockalert.StockAlert.ImageAdapter;
import com.pocketools.stockalert.service.OnAlarmReceiver;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AlertList extends ListActivity {

    public static final int DIALOG_HIGH_ALERT_NOT_SAVED = 1000;
    public static final int DIALOG_LOW_ALERT_NOT_SAVED = 1001;
    //public static final String ANALYTICS_UA_NUMBER = "UA-12361531-2"; 
	private static final int MENU_INFO = 1004;
	private static final int MENU_SETTINGS = 1005;
    
    public static final int PERIOD=900000; // 15 minutes
    
    
	
	Intent mIntent;
    private DBAdapter mDb;
    ArrayList<String> mCurrencyList;
    View mFooterView;
    //String mSymbol;
    String mCompanyName;
    TextView mHeadingView;
    //TextView mXrateUpdateMessage;
    String mRefreshDateText = "";

    GoogleAnalyticsTracker mTracker;
    String mCurrencyString;
    //AdView mAdView;
    
 // Replace with your own AdSense client ID.
    private static final String CLIENT_ID = "ca-mb-app-pub-1805290976571198";

    // Replace with your own company name.
    private static final String COMPANY_NAME = "DELADS";

    // Replace with your own application name.
    private static final String APP_NAME = "Stock Alert";

    // Replace with your own keywords used to target Google ad.
    // Join multiple words in a phrase with '+' and join multiple phrases with ','.
    private String mKeywords = "spread betting";

    // Replace with your own AdSense channel ID.
    private static final String CHANNEL_ID = "7194933065";
    
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);  
        
        mTracker = GoogleAnalyticsTracker.getInstance();        
        // Start the tracker in manual dispatch mode...
        mTracker.start(StockAlert.ANALYTICS_UA_NUMBER, this);
        mTracker.trackPageView("/AlertList");
        
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);       
        setContentView(R.layout.alert_list_header);
           
        mDb = new DBAdapter(this);
        mDb.open();
        
        
        /*
        Cursor defaults = mDb.getDefaultValues();
        startManagingCursor(defaults);
        
        for(int i=0; i< defaults.getCount(); i++){
        	defaults.moveToPosition(i);
        	
        	String default_type = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_TYPE)); 
        	if(default_type.compareTo("google_keywords") ==0){
        		mKeywords = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	}
        		
        }
        */

    	Cursor c = mDb.getStocks();      
        startManagingCursor(c);
        
        ListAdapter listAdapter = new MyCurrencyAdapter(this,c);       
        ListView listView = getListView();
        
        TextView view = new TextView(this);
        view.setHeight(250);
        view.setBackgroundColor(Color.BLACK);
        
        listView.addFooterView(view);
        
        
        listView.setAdapter(listAdapter);
        
        
        
        Gallery g = (Gallery) findViewById(R.id.gallery_alert_list);
        g.setAdapter(new ImageAdapter(this));
        
        g.setSelection(4);
        g.setSpacing(0);
        
        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
            	
            	((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(30);
            	
            	if(position == 0){
            		Intent intent = new Intent(v.getContext(),Currencies.class);
            		startActivity(intent);
            		
            		finish();
            	}
            	
            	if(position == 1){
            		Intent intent = new Intent(v.getContext(),Commodities.class);
            		startActivity(intent);
            		
            		finish();
            	}
            	
            	if(position == 2){
            		Intent intent = new Intent(v.getContext(),Markets.class);
            		intent.putExtra(Markets.IS_COMING_FROM_NAVIGATION, true);
            		startActivity(intent);
            		
            		finish();
            	}
            	
            	if(position == 3){
            		Intent intent = new Intent(v.getContext(),StockAlert.class);
            		startActivity(intent);
            		
            		finish();
            	}
            }
        });

        
        try{
        	/*
	        // Set up GoogleAdView.
	        GoogleAdView adView = (GoogleAdView) findViewById(R.id.adview_alert_list);
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
        	
        	TableLayout layout = (TableLayout) findViewById(R.id.alert_list_table_layout); 
        	//AdWhirlLayout adWhirlLayout = new AdWhirlLayout(this, "db0a5aff9397431ebb02e8d023a607cb"); 
        	AdWhirlLayout adWhirlLayout = new AdWhirlLayout(this, "3b5a5a364eb14d6c80d2fade1372844e"); 
        	adWhirlLayout.setBackgroundColor(Color.BLACK);

*/
        	
        	final int DIP_WIDTH = 320; 
        	final int DIP_HEIGHT = 52; 
        	final float DENSITY = getResources().getDisplayMetrics().density; 
        	int scaledWidth = (int) (DENSITY * DIP_WIDTH + 0.5f); 
        	int scaledHeight = (int) (DENSITY * DIP_HEIGHT + 0.5f); 
        	
        	RelativeLayout.LayoutParams adWhirlLayoutParams = 
        	    new RelativeLayout.LayoutParams(scaledWidth, scaledHeight); 
        	
        	//layout.addView(adWhirlLayout, adWhirlLayoutParams); 
        	//layout.invalidate(); 
        
        }catch(Exception e){
   		 Log.e(getLocalClassName(),"Error Retriving Google Ad");
   	 }
    
        
        
        
        mTracker.dispatch();       
    }
    
    
    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;

        private Integer[] mImageIds = {
        		R.drawable.currencies_off,
        		R.drawable.commodities_off,
        		R.drawable.markets_off,
                R.drawable.portfolio_off,
                R.drawable.alerts_on
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
           // i.setBackgroundResource(0);
           // i.setLayoutParams(new Gallery.LayoutParams(150, 40));
          // i.setScaleType(ImageView.ScaleType.FIT_XY);
          //  i.setBackgroundResource(mGalleryItemBackground);

            return i;
        }
    }
    
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
	    	
	    	
	    	menu.add(0,MENU_INFO,0,"Info")
	 		.setIcon(android.R.drawable.ic_menu_help);
	    	
	    	menu.add(0,MENU_SETTINGS,0,"Settings")
	 		.setIcon(android.R.drawable.ic_menu_edit);
	    	
    	  return result;

    }
    
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {   
        
			
	    case MENU_INFO:
	    	
	    	Intent intent = new Intent (this,Info.class);
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
  
 
   
    
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {      
        Log.d(this.getLocalClassName(), "onSaveInstanceState()");
        
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onPause() {
        Log.d(this.getLocalClassName(), "onPause()");

        
        if(mDb.getDatabase().isOpen())
        	mDb.close();

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
        
        super.onDestroy();
        mTracker.stop();
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
    	
    	if(!mDb.getDatabase().isOpen())
    		mDb.open();

    	super.onStart();
    }
    
    private abstract class MyCustomViewListener implements View.OnClickListener{
    	
    	CheckBox mCheckBox;
    	EditText mEditText;
    	TextView mTextView;
    	String mPrice;
    	String mSymbol;
    	
    	public MyCustomViewListener(CheckBox checkbox,EditText editText, TextView textView, String price, String symbol){
    		mCheckBox = checkbox;
    		mEditText = editText;
    		mTextView = textView;
    		mPrice = price;
    		mSymbol = symbol;
    	}

    	protected CheckBox getCheckBox(){return mCheckBox;}
    	protected EditText getEditText(){return mEditText;}
    	protected TextView getTextView(){return mTextView;}
    	protected String getPrice(){return mPrice;}
    	protected String getSymbol(){return mSymbol;}
    }
    
    
   
    
    
    private class MyCurrencyAdapter extends CursorAdapter{
    	private Cursor mCursor;
    	private Context mContext;
    	private final LayoutInflater mInflater;
    	//private EditText mAlertListHigh;
    	//private EditText mAlertListLow;
    	//private CheckBox mHighCheck;
    	//private CheckBox mLowCheck;
    	//private TextView mAlertHighText;
    	//private TextView mAlertLowText;
    	//private String mPrice;
    	//private String mSymbolName;
 
    	public MyCurrencyAdapter(Context context, Cursor cursor) {
            super(context, cursor, true);
            mInflater = LayoutInflater.from(context);
            mContext = context;
    	}
    	
    	
 
    	@Override
    	public void bindView(View view, Context context, Cursor cursor) {
    		// TODO Auto-generated method stub
    		
    			String symbol = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_SYMBOL));
    			mCompanyName = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_COMPANY_NAME));
    			String companyNameStart = mCompanyName;
    			
    			if(mCompanyName != null){
    				
    				StringTokenizer tokenizer = new StringTokenizer(mCompanyName," ");
    				if(tokenizer.hasMoreTokens()){
    					companyNameStart = tokenizer.nextToken();
    				}  				
    			}
    			
    			TableLayout table = (TableLayout)view.findViewById(R.id.alert_list_layout_content);
    			table.setClickable(true);
		
    			String price = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_LAST_TRADE_PRICE));
    		
	    		TextView t = (TextView) view.findViewById(R.id.alert_list_symbol);
	         	t.setText(symbol);
	            
	           	t = (TextView) view.findViewById(R.id.alert_list_last_trade);
	            t.setText(price);
	            
	            EditText mAlertListHigh = (EditText) view.findViewById(R.id.alert_list_high_edit_text);
	            EditText mAlertListLow = (EditText) view.findViewById(R.id.alert_list_low_edit_text);
	            CheckBox mHighCheck = (CheckBox)view.findViewById(R.id.alert_list_high_checkbox);
	            CheckBox mLowCheck = (CheckBox)view.findViewById(R.id.alert_list_low_checkbox);
	            TextView mAlertHighText = (TextView)view.findViewById(R.id.alert_list_high_text);
	            TextView mAlertLowText = (TextView)view.findViewById(R.id.alert_list_low_text);
	            
	            
	            
	            
	            Cursor stockAlerts = mDb.getAlerts(symbol);
	            	            startManagingCursor(stockAlerts);

	            
	            if(stockAlerts.getCount() > 0){            	
	            	stockAlerts.moveToFirst();
	            	
	            	Double highPrice = stockAlerts.getDouble((stockAlerts.getColumnIndex(DBAdapter.KEY_HIGH_ALERT_PRICE)));	            	
	            	mAlertListHigh.setText(highPrice.toString());	            	
	            	int highAlertisActive = stockAlerts.getInt((stockAlerts.getColumnIndex(DBAdapter.KEY_HIGH_ALERT_ACTIVE)));	            		            	
	            	
	            	if(highAlertisActive == 1){
	            		
	            		mHighCheck.setChecked(true);
	            		mAlertHighText.setText("Active");
	            		mAlertHighText.setTextColor(Color.GREEN);
	            	}
	            	else{
	            		mHighCheck.setChecked(false);
	            		mAlertHighText.setText("Inactive");
	            		mAlertHighText.setTextColor(Color.RED);
	            	}
	            	
	            	
	            	Double lowPrice = stockAlerts.getDouble((stockAlerts.getColumnIndex(DBAdapter.KEY_LOW_ALERT_PRICE)));
	            	mAlertListLow.setText(lowPrice.toString());
	            	
	            	int lowAlertisActive = stockAlerts.getInt((stockAlerts.getColumnIndex(DBAdapter.KEY_LOW_ALERT_ACTIVE)));	            	
	            	mLowCheck = (CheckBox)view.findViewById(R.id.alert_list_low_checkbox);

	            	
	            	if(lowAlertisActive == 1){
	            		mLowCheck.setChecked(true);
	            		mAlertLowText.setText("Active");
	            		mAlertLowText.setTextColor(Color.GREEN);
	            	}
	            	else{
	            		mLowCheck.setChecked(false); 
	            		mAlertLowText.setText("Inactive");
	            		mAlertLowText.setTextColor(Color.RED);
	            	}
	            }
	            
	            
	          //If there's any change to the price, we disable the alerts
	            StockUtil util = new StockUtil(mHighCheck,mAlertHighText, symbol);
	            mAlertListHigh.setTag(util);
	           	            
	            mAlertListHigh.setOnKeyListener(new EditText.OnKeyListener() {

	            	public boolean onKey(View v, int keyCode, KeyEvent event) {
	            		
	            		if(keyCode != KeyEvent.KEYCODE_BACK){
		            		StockUtil util = (StockUtil)v.getTag();
		            		CheckBox checkbox = util.getCheckBox();
		            		TextView textView = util.getTextView();
		            		String mSymbol = util.getSymbol();
		            		
		            		checkbox.setChecked(false);
		            		textView.setText("Inactive");
		            		textView.setTextColor(Color.RED);
		            		mDb.activateHighAlert(mSymbol, false);
	            		}
	            		
	            		return false;
	            	}
	            });
	            
	            
		          //If there's any change to the price, we disable the alerts
	            util = new StockUtil(mLowCheck,mAlertLowText, symbol);
	            mAlertListLow.setTag(util);
	           	            
	            mAlertListLow.setOnKeyListener(new EditText.OnKeyListener() {

	            	public boolean onKey(View v, int keyCode, KeyEvent event) {
	            		
	            		if(keyCode != KeyEvent.KEYCODE_BACK){
		            		StockUtil util = (StockUtil)v.getTag();
		            		CheckBox checkbox = util.getCheckBox();
		            		TextView textView = util.getTextView();
		            		String mSymbol = util.getSymbol();
		            		
		            		checkbox.setChecked(false);
		            		textView.setText("Inactive");
		            		textView.setTextColor(Color.RED);
		            		mDb.activateLowAlert(mSymbol, false);
	            		}
	            		
	            		return false;
	            	}
	            });
           
	            
	            mHighCheck.setOnClickListener(new MyCustomViewListener(mHighCheck,mAlertListHigh,mAlertHighText, price, symbol) {
	                public void onClick(View v) {
	                	
	                	CheckBox mHighCheck = getCheckBox();
	                	EditText mAlertListHigh = getEditText();
	                	TextView mAlertHighText = getTextView();
	                	String price = getPrice().replace(",", "");
	              	
	                    // Perform action on clicks
	                    if (mHighCheck.isChecked()) {   
	                    	
	                    	//Let's hide the keyboard if it's still showing
	                    	InputMethodManager imm = (InputMethodManager)getSystemService(v.getContext().INPUT_METHOD_SERVICE);
	                    	imm.hideSoftInputFromWindow(mAlertListHigh.getWindowToken(), 0);
	                    	
	                    	
	                    	//Let's make sure the user has SOMETHING in here in the first place
	                    	if(mAlertListHigh.getText().toString().length() < 1)
	                    		mAlertListHigh.setText("0");
	                    	
	                    	//Let's check to make sure the high alert is higher than the current price
	                    	if(Double.parseDouble(mAlertListHigh.getText().toString()) > Double.parseDouble(price)){
	    	
	                    		mDb.activateHighAlert(mSymbol, true, mAlertListHigh.getText().toString());
	                    		mAlertHighText.setText("Active");
	                        	mAlertHighText.setTextColor(Color.GREEN);
	                        	
	                        	Toast.makeText(v.getContext(), "Alert activated at " + mAlertListHigh.getText().toString(), Toast.LENGTH_SHORT).show();                    	
	                    	
	                        	Cursor alerts = null;
	                    	    alerts = mDb.getActiveAlerts();
	                    	    
	                    	    //If we don't have any previous alerts, we need to start the Alerts System
	                    	    if(alerts.getCount() == 1){
	                    	    	
	                    	    	AlarmManager mgr=(AlarmManager)v.getContext().getSystemService(Context.ALARM_SERVICE);
	                    			Intent alertIntent=new Intent(v.getContext(), OnAlarmReceiver.class);
	                    			PendingIntent alertPI=PendingIntent.getBroadcast(v.getContext(), 0,alertIntent, 0);
	                    			
	                    			//Let's make sure we are as reliable as possible
	                    			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+10000,Markets.PERIOD,alertPI);
	                    			
	                    			
	                    			Log.d("Stock Alert Pro","Starting / Restarting the Alerts System");    
	                    			
	                    	    	
	                    	    }
	                    	    alerts.close();
	                    	}
	                    	
	                    	else{                		
	                    		mHighCheck.setChecked(false);
	                    		mAlertHighText.setText("Inactive");
	                        	mAlertHighText.setTextColor(Color.RED);
	                        	
	                        	showDialog(DIALOG_HIGH_ALERT_NOT_SAVED);
	                        	
	                    		//Toast.makeText(v.getContext(), "ERROR: Alert needs to be above current price of " + mPrice, Toast.LENGTH_LONG).show();
	                        	
	                    	
	                    	}
	                    	
	                    
	                    	
	                    } else {
	                    	mDb.activateHighAlert(mSymbol, false);
	                    	mAlertHighText.setText("Inactive");
	                    	mAlertHighText.setTextColor(Color.RED);
	                    	
	                    	Toast.makeText(v.getContext(), "Alert removed", Toast.LENGTH_SHORT).show();
	                    	
	                    	
	                    	Cursor alerts = null;
                    	    alerts = mDb.getActiveAlerts();
                    	    
                    	    
	                    	//If we don't have any other alerts, Let's shut down the Alerts System 
                    	    if(alerts.getCount() == 0){
                    	    	
                    	    	AlarmManager mgr=(AlarmManager)v.getContext().getSystemService(Context.ALARM_SERVICE);
                    			Intent alertIntent=new Intent(v.getContext(), OnAlarmReceiver.class);
                    			PendingIntent alertPI=PendingIntent.getBroadcast(v.getContext(), 0,alertIntent, 0);
                    			
                    			//Let's make sure we are as reliable as possible
                    			mgr.cancel(alertPI);
                    			
                    			
                    			Log.d("Stock Alert Pro","Shutting down Alerts System");    
                    			
                    	    	
                    	    }
                    	    
                    	    alerts.close();
	                        
	                    }
	                }
	            });
	            
	            mLowCheck.setOnClickListener(new MyCustomViewListener(mLowCheck,mAlertListLow,mAlertLowText,price, symbol) {
	                public void onClick(View v) {
	                	
	                	CheckBox mLowCheck = getCheckBox();
	                	EditText mAlertListLow = getEditText();
	                	TextView mAlertLowText = getTextView();
	                	String price = getPrice().replace(",", "");
	              	
	                    // Perform action on clicks
	                    if (mLowCheck.isChecked()) { 
	                    	
	                    	//Let's hide the keyboard if it's still showing
	                    	InputMethodManager imm = (InputMethodManager)getSystemService(v.getContext().INPUT_METHOD_SERVICE);
	                    	imm.hideSoftInputFromWindow(mAlertListLow.getWindowToken(), 0);
	                    	
	                    	//Let's make sure the user has SOMETHING in here in the first place
	                    	if(mAlertListLow.getText().toString().length() < 1)
	                    		mAlertListLow.setText("0");
	                    	
	                    	//Let's check to make sure the high alert is higher than the current price
	                    	if(Double.parseDouble(mAlertListLow.getText().toString()) < Double.parseDouble(price)){
	                  		
	                    		mDb.activateLowAlert(mSymbol, true, mAlertListLow.getText().toString());
	                    		mAlertLowText.setText("Active");
	                        	mAlertLowText.setTextColor(Color.GREEN);
	                        	
	                        	Toast.makeText(v.getContext(), "Alert activated at " + mAlertListLow.getText().toString(), Toast.LENGTH_SHORT).show();                    	
	                        	
	                        	
	                        	Cursor alerts = null;
	                    	    alerts = mDb.getActiveAlerts();
	                    	    
	                    	    //If we don't have any previous alerts, we need to start the Alerts System
	                    	    if(alerts.getCount() == 1){
	                    	    	
	                    	    	AlarmManager mgr=(AlarmManager)v.getContext().getSystemService(Context.ALARM_SERVICE);
	                    			Intent alertIntent=new Intent(v.getContext(), OnAlarmReceiver.class);
	                    			PendingIntent alertPI=PendingIntent.getBroadcast(v.getContext(), 0,alertIntent, 0);
	                    			
	                    			//Let's make sure we are as reliable as possible
	                    			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+10000,Markets.PERIOD,alertPI);
	                    			
	                    			
	                    			Log.d("Stock Alert Pro","Starting / Restarting the Alerts System");    
	                    			
	                    	    	
	                    	    }
	                    	    alerts.close();
	                    	
	                    	}
	                    	
	                    	else{
	                    		mLowCheck.setChecked(false);
	                    		mAlertLowText.setText("Inactive");
	                        	mAlertLowText.setTextColor(Color.RED);
	                        	
	                        	showDialog(DIALOG_LOW_ALERT_NOT_SAVED);
	                    		//Toast.makeText(v.getContext(), "ERROR: Alert needs to be below current price of " + mPrice, Toast.LENGTH_LONG).show();
	                    	}
	                    	
	                    } else {
	                    	mDb.activateLowAlert(mSymbol, false);
	                    	mAlertLowText.setText("Inactive");
	                    	mAlertLowText.setTextColor(Color.RED);
	                    	
	                    	Toast.makeText(v.getContext(), "Alert removed", Toast.LENGTH_SHORT).show();
	                    	
	                    	Cursor alerts = null;
                    	    alerts = mDb.getActiveAlerts();
                    	    
                    	    
	                    	//If we don't have any other alerts, Let's shut down the Alerts System 
                    	    if(alerts.getCount() == 0){
                    	    	
                    	    	AlarmManager mgr=(AlarmManager)v.getContext().getSystemService(Context.ALARM_SERVICE);
                    			Intent alertIntent=new Intent(v.getContext(), OnAlarmReceiver.class);
                    			PendingIntent alertPI=PendingIntent.getBroadcast(v.getContext(), 0,alertIntent, 0);
                    			
                    			//Let's make sure we are as reliable as possible
                    			mgr.cancel(alertPI);
                    			
                    			
                    			Log.d("Stock Alert Pro","Shutting down Alerts System");    
                    			
                    	    	
                    	    }
                    	    
                    	    alerts.close();
	                    }
	                }
	            });
	      
	            
	            
	            
    	}
    	
    	 
    	@Override
    	public View newView(Context context, Cursor cursor, ViewGroup parent) {
    		// TODO Auto-generated method stub
    		
	    		final View view = mInflater.inflate(R.layout.alert_list_content, parent, false);
	    		return view;
    	}
    	
    	

    } 
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_HIGH_ALERT_NOT_SAVED:
            return new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_alert_not_saved_title)
                .setMessage(R.string.dialog_high_alert_not_saved_message)
                .setPositiveButton(R.string.dialog_alert_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
            
        case DIALOG_LOW_ALERT_NOT_SAVED:
            return new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_alert_not_saved_title)
                .setMessage(R.string.dialog_low_alert_not_saved_message)
                .setPositiveButton(R.string.dialog_alert_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();

         default:
        	 return null;
        }
    }
    
    
}