package com.pocketools.stockalert;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.ads.*;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.pocketools.stockalert.service.OnAlarmReceiver;
import com.pocketools.stockalert.service.OnWidgetAlarmReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Config;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.MultiAutoCompleteTextView.Tokenizer;
import android.widget.RelativeLayout.LayoutParams;


public class Markets extends Activity {
    /** Called when the activity is first created. */

	private DBAdapter mDb;
	private ImageButton mImage;
	private TableRow mGraphTimeframesRow;
	private TableRow mCompanyNewsExpandable;
	private TableRow mYouTubeExpandable;
	private boolean mCompanyNewsToggleExpanded = true;
	private boolean mYouTubeToggleExpanded = false;
	private ImageView mCompanyNewsArrow;
	private ImageView mYouTubeArrow;
	private String mSymbol = "";
	private String mExchange = "";
	private String mCompanyNameStart = "";
	private String mCompanyName="";
	
	
	private String mPrice;
	private static String YAHOO_ID = ".LRljOPV34HotkuMkLW2IRCXRn5UvSoUQscvtgmwA550PVIEAOQm7awmxCoifyiq6TYmUMx8aQbXOg--";
	public static String GROUPON_CLIENT_ID = "1b275b4fea756d0e9cd2ebb4f2fba9a42df320d3";
	private TableLayout mCompanyNewsTable;
	private TableLayout mYouTubeNewsTable;
	private Context mContext;
	private boolean mStillUpdating = false;
	private TextView mStockDetailCompanyName;
	private TextView mStockDetailPrice;
	private TextView mStockDetailChange;
	private TextView mStockDailyHigh;
	private TextView mStockDailyLow;
	private TextView mStockDailyVolume;
	private TextView mStockLastTradeTime;
	private AnimationDrawable mGraphAnimation;
	private AnimationDrawable mCompanyInfoAnimation;
	private AnimationDrawable mYouTubeAnimation;
	private boolean mChartDisplayed = false;
	private boolean mCompanyInfoDisplayed = false;
	private boolean mYouTubeDisplayed = false;
	private String mDefaultTimeframe = "1d";
	private String mDefaultLocation = "Current Location";
	private NetworkInfo mNetworkInfo = null;
	private ImageView mGraphLoading;
	private ImageView mCompanyInfoLoading;
	private ImageView mYouTubeLoading;
	private HashMap<Integer,Bitmap> mYouTubeThumbnails;
	private String mVideoFormat = "5";
	private int mThumbnailCount = 0;
	private GoogleAnalyticsTracker mTracker;
//	private DownloadKeywordsTask mKeywordsTask ;
	private DownloadNewsTask mDownloadNewsTask;
	private DownloadSingleStockTask mSingleStockTask;
	private DownloadYouTubeTask mDownloadYouTubeTask;
	
	private static final int MENU_REFRESH = 1001;
	private static final int MENU_CHANGE_INDEX = 1002;
	private static final int MENU_ADD_TO_PORTFOLIO = 1003;
	private static final int MENU_INFO = 1004;
	private static final int MENU_SETTINGS = 1005;
	public static final int ALERT_DIALOG_USER_AGREEMENT = 1010;
	private static final int SHOW_ADDED_TO_PORTFOLIO = 1011;
	private static final int MENU_ADS = 1100;
	
	
	public static final String MARKETS_PAGE = "Markets Page";
    public static final String PORTFOLIO_PAGE = "Portfolio Page";
    public static final String ALERTS_PAGE = "Alerts Page";
    public static final String COMMODITIES_PAGE = "Commodities Page";
    public static final String CURRENCY_PAGE = "Currency Page";
    public static final String IS_COMING_FROM_NAVIGATION = "Navigation";
    public static final int PERIOD=900000; // 15 minutes
    
    public static final String CJ_AFFILIATE = "http://www.anrdoezrs.net/click-4188942-10804307?url=";
    
    
    private String mAdPreferences = "";

    //Increment this by 1 every time you want to show a splash screen to the user
    //The splash screen will only show once ever, unless we increment
    int mSplashScreenVersion = 5;
    
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
    private static final String CHANNEL_ID = "8890559790";
	
	

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mTracker = GoogleAnalyticsTracker.getInstance();        
        // Start the tracker in manual dispatch mode...
        mTracker.startNewSession(StockAlert.ANALYTICS_UA_NUMBER, this);
        mTracker.trackPageView("/Markets");
        
        
        mContext = this;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        setContentView(com.pocketools.stockalert.R.layout.markets); 
        
        
        
        
        mDb = new DBAdapter(this);
        mDb.open();
        
        boolean hasPageSet = false;
        boolean hasKeywordsSet = false;
        boolean hasDefaultTimeframeSet = false;
        boolean hasDefaultLocationSet = false;
        boolean hasAdPreferencesSet = false;
        String defaultPage = "";
        
        boolean hasAlertFrequencySet = false;
        String alertFrequency = "";
        
        
        //Only set an alarm if we have widgets set
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("widget_preferences", 0);
	    Map<String,?> widgetMap = prefs.getAll();
	    
	    Set<String> symbols = widgetMap.keySet();
	    Iterator<String> iter = symbols.iterator();
	    
	    
	 
		
	    if(iter.hasNext()){ //We reset the alarm just incase the user has killed the service by mistake
	    	
	    	 //This will create a new alarm - replacing the existing alarm if already present
	    	AlarmManager mgr=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
			Intent alarmIntent=new Intent(this, OnWidgetAlarmReceiver.class);
			PendingIntent pi=PendingIntent.getBroadcast(this, 0,alarmIntent, 0);
			
			mgr.setRepeating(AlarmManager.RTC,SystemClock.elapsedRealtime()+10000,PERIOD,pi); 			
			//Log.d(getLocalClassName(),"We have a widget so the alarm is reset");
        
        
	    }
	    else{
	    	
	    	AlarmManager mgr=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
			Intent alarmIntent=new Intent(this, OnWidgetAlarmReceiver.class);
			PendingIntent pi=PendingIntent.getBroadcast(this, 0,alarmIntent, 0);
	    	mgr.cancel(pi);
	    	//Log.d(getLocalClassName(),"Since we have no more Widgets, we remove any existing Alarms");
	    }
	    
        
        
	  //Let's restart the Alerts system to make sure everything is running smoothly. 
	    Cursor alerts = null;
	    alerts = mDb.getActiveAlerts();
	    
	    //If we don't have any previous alerts, we need to start the Alerts System
	    if(alerts.getCount() > 0){
	    	//This will create a new alarm - replacing the existing alarm if already present
	    	AlarmManager mgr=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
			Intent alarmIntent=new Intent(this, OnAlarmReceiver.class);
			PendingIntent pi=PendingIntent.getBroadcast(this, 0,alarmIntent, 0);
			 
			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+60000,PERIOD,pi); 
			
			//Log.d("Stock Alert","Starting / Restarting the Alerts System");    
			
	    }
	    alerts.close();
        
        
        Cursor defaults = mDb.getDefaultValue("default_market_symbol");
        startManagingCursor(defaults);
        
        if(defaults.getCount() < 1)
        	mDb.addDefaultPair("default_market_symbol", "^IXIC");
        
        

        defaults = mDb.getDefaultValues();
        startManagingCursor(defaults);
        
        for(int i=0; i< defaults.getCount(); i++){
        	defaults.moveToPosition(i);
        	
        	String default_type = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_TYPE)); 

        	
        	if(default_type.compareTo("company_news_section_expanded") ==0){
        		String graphExpanded = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		if(graphExpanded.compareTo("on")==0)
        			mCompanyNewsToggleExpanded = true;
        		
        		else
        			mCompanyNewsToggleExpanded = false;
        	}
        	
        	else if(default_type.compareTo("youtube_section_expanded") ==0){
        		String graphExpanded = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		if(graphExpanded.compareTo("on")==0)
        			mYouTubeToggleExpanded = true;
        		
        		else
        			mYouTubeToggleExpanded = false;
        	}
        	
        	
        	else if(default_type.compareTo("default_market_symbol") ==0){
        		mSymbol = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	}
        	
        	
        	else if(default_type.compareTo("user_agreement_accepted") ==0){
        		String accepted = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		
        		if(accepted.compareTo("false") == 0){
        			showDialog(ALERT_DIALOG_USER_AGREEMENT);
        			
        		}
        		else{
        			
        			int currentVersion = mDb.getSplashScreenVersion();
        			if(currentVersion < mSplashScreenVersion){
        				mDb.updateSplashScreenVersion(mSplashScreenVersion);
        				
        				Intent FAQIntent = new Intent(this,FAQ.class);
                        startActivity(FAQIntent);
        			}
        			
        		}
        	} 
        	
        	else if(default_type.compareTo("default_page") ==0){
        		defaultPage = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		hasPageSet = true;
        	}
        	
        	/*
        	else if(default_type.compareTo("google_keywords") ==0){
        		mKeywords = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		hasKeywordsSet = true;
        	}
        	*/
        	
        	else if(default_type.compareTo("alert_frequency") ==0){
        		alertFrequency = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		hasKeywordsSet = true;
        	}
        	
        	else if(default_type.compareTo("default_timeframe") ==0){
        		mDefaultTimeframe = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		hasDefaultTimeframeSet = true;
        	}
        	
        	else if(default_type.compareTo("default_location") ==0){
        		mDefaultLocation = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		hasDefaultLocationSet = true;
        	}
        	
        	
        	else if(default_type.compareTo("ad_preferences") ==0){
        		mAdPreferences = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		hasAdPreferencesSet = true;
        	}
        	
        	
        	
        	
        }
        
        if(!hasPageSet){
        	defaultPage = Markets.MARKETS_PAGE;
        	mDb.addDefaultPage(Markets.MARKETS_PAGE);
        }
        
        if(!hasDefaultTimeframeSet){
        	mDb.addDefaultTimeframe(mDefaultTimeframe);
        }
        
        if(!hasDefaultLocationSet){
        	mDb.addDefaultLocation(mDefaultLocation);
        }
        
        if(!hasAdPreferencesSet){
        	mDb.addAdPreferences(mAdPreferences);
        }
        else
        	Log.d("Stock Alert","Ads Preferences = " + mAdPreferences);
        
        
        
        
        /*
        
        if(!hasKeywordsSet){
        	mDb.addGoogleKeywords(mKeywords);
        }
        
        */
        
        
        boolean isInternalNavigation = this.getIntent().getBooleanExtra(Markets.IS_COMING_FROM_NAVIGATION, false);
        
        if(!isInternalNavigation){
	        if(defaultPage.compareTo(Markets.ALERTS_PAGE) == 0){
	        	Intent intent = new Intent(this,AlertList.class);
	        	startActivity(intent);
	        	finish();
	        }
	        else if(defaultPage.compareTo(Markets.PORTFOLIO_PAGE) == 0){
	        	Intent intent = new Intent(this,StockAlert.class);
	        	startActivity(intent);
	        	finish();
	        }
	        else if(defaultPage.compareTo(Markets.COMMODITIES_PAGE) == 0){
	        	Intent intent = new Intent(this,Commodities.class);
	        	startActivity(intent);
	        	finish();
	        }
	        else if(defaultPage.compareTo(Markets.CURRENCY_PAGE) == 0){
	        	Intent intent = new Intent(this,Currencies.class);
	        	startActivity(intent);
	        	finish();
	        }
        }

        if(this.getIntent().hasExtra("SYMBOL")){
        	mSymbol = this.getIntent().getExtras().getString("SYMBOL");
        	mDb.updateDefaultValue("default_market_symbol", mSymbol);
        }
       
        mExchange = "";
             

        
        if(this.getIntent().hasExtra("COMPANY_NAME"))
        	mCompanyName = this.getIntent().getExtras().getString("COMPANY_NAME");

        try{
	        StringTokenizer tokenizer = new StringTokenizer(mCompanyName," ");
			if(tokenizer.hasMoreTokens()){
				mCompanyNameStart = tokenizer.nextToken();
			}
        }catch(Exception e){}
        
        
     //   Log.d(getLocalClassName(),"Company name = '" + mCompanyNameStart + "'");
		
		mYouTubeThumbnails = new HashMap<Integer, Bitmap>();
        
        
        //mStillUpdating = this.getIntent().getExtras().getBoolean("STILL_UPDATING");
		mStillUpdating = true;
			

		
		
		
		Gallery g = (Gallery) findViewById(R.id.gallery_markets);
        g.setAdapter(new ImageAdapterMarkets(mContext));
        
        g.setSelection(2);
        g.setSpacing(0);
        
        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
            	
            	((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(30);
            	
            	if(position == 0){
            		Intent intent = new Intent (v.getContext(),Currencies.class);
            		intent.putExtra("STILL_UPDATING", mStillUpdating);
        			startActivity(intent);  
        			finish();
            	}
            	
            	if(position == 1){
            		Intent intent = new Intent (v.getContext(),Commodities.class);
        			startActivity(intent);  
        			finish();
            	}
            	
            	if(position == 3){
            		Intent intent = new Intent (v.getContext(),StockAlert.class);
            		intent.putExtra("STILL_UPDATING", mStillUpdating);
        			startActivity(intent);  
        			finish();
            	}
            	
            	else if(position == 4){
            		Intent intent = new Intent (v.getContext(),AlertList.class);
            		intent.putExtra("STILL_UPDATING", mStillUpdating);
        			startActivity(intent);  
        			finish();
            	}
            }
        });
   
        
        
        TextView textView = (TextView)findViewById(R.id.markets_timeframe_1d);      
        if(mDefaultTimeframe.compareTo("1d") == 0)  	  
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        
        textView = (TextView)findViewById(R.id.markets_timeframe_5d);
        if(mDefaultTimeframe.compareTo("5d") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        textView = (TextView)findViewById(R.id.markets_timeframe_3m);
        if(mDefaultTimeframe.compareTo("3m") == 0)
      	  textView.setTextColor(Color.YELLOW);   	 
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        textView = (TextView)findViewById(R.id.markets_timeframe_1y);
        if(mDefaultTimeframe.compareTo("1y") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        textView = (TextView)findViewById(R.id.markets_timeframe_2y);
        if(mDefaultTimeframe.compareTo("2y") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        
        textView = (TextView)findViewById(R.id.markets_timeframe_5y);
        if(mDefaultTimeframe.compareTo("5y") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        
		ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = serviceConn.getActiveNetworkInfo();
	    
        
        
        if(mStillUpdating){
        	
        	
        	String symbol = mSymbol;
        	
        	//Fix for the DOW
        	if(mSymbol.compareToIgnoreCase("^DJI") == 0){
        		symbol = "INDU";
        	}
        	
	        String url = "http://finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=sxl1c1p2t1d1hgkjj1ab2bb3pot8va2ren";
	        mSingleStockTask = new DownloadSingleStockTask();
	        mSingleStockTask.execute(url);
        }
        
        
        
        mImage = (ImageButton)findViewById(com.pocketools.stockalert.R.id.graph_small);
        mGraphTimeframesRow = (TableRow)findViewById(R.id.market_graph_timeframe);
        
        
        
        if(mNetworkInfo != null){
        	
        	/*
        	mKeywordsTask = new DownloadKeywordsTask();
        	mKeywordsTask.execute("http://www.pocketsmart.net/stockalert/keywords.txt");
        	*/
        	
        	
	        //new DownloadImageTask().execute("http://ichart.finance.yahoo.com/t?s=" + mSymbol);
	        new DownloadImageTask().execute("http://ichart.finance.yahoo.com/z?s=" + mSymbol + "&t=" + mDefaultTimeframe + "&q=l&l=off&z=m&a=v&p=s");
        
        mCompanyNewsTable = (TableLayout)findViewById(com.pocketools.stockalert.R.id.company_news_table);
        //String url = "http://search.yahooapis.com/NewsSearchService/V1/newsSearch?appid=" + YAHOO_ID + "&query=" + mExchange + ":" + mSymbol + "&type=any&results=20&language=en&sort=date";
       
        String queryString = "";
        
        if(mSymbol.contains("^"))
        	queryString = mSymbol;
        else if (mSymbol.contains("=X")){
        	int c = mSymbol.indexOf("=X");
        	queryString = mSymbol.substring(0,c);
        	
        }
        else
        	queryString = mExchange + ":" + mSymbol;
        
        String url = "http://www.google.com/finance/company_news?q=" + queryString + "&output=json&num=20";
        
        
        mDownloadNewsTask = new DownloadNewsTask();
        mDownloadNewsTask.execute(url);
        
        
        mYouTubeNewsTable = (TableLayout)findViewById(com.pocketools.stockalert.R.id.youtube_table);
        
        
        url = "http://gdata.youtube.com/feeds/api/videos?q=" +
        mCompanyNameStart + 
        "+company&orderby=relevance&start-index=1&max-results=10&v=2&alt=jsonc&category=News&time=this_week&safeSearch=strict&format=" + mVideoFormat;
       
       
        
        mDownloadYouTubeTask = new DownloadYouTubeTask();
        mDownloadYouTubeTask.execute(url);
        
        
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
    	deals.execute("http://api.groupon.com/v2/deals.json?client_id=" + GROUPON_CLIENT_ID + urlExtension);
       
        
        
        }  // if(mNetworkInfo != null){
        else{
        	Log.d(getLocalClassName(),"Connection Error");
	    	Toast.makeText(this, "Connection Error. Please check your connections and try again.", Toast.LENGTH_SHORT).show();
        }	
        
        
        mStockDetailCompanyName = (TextView)findViewById(R.id.stock_detail_company_name);
        mStockDetailCompanyName.setText("Updating ..");
        
        mStockDetailPrice = (TextView)findViewById(R.id.stock_detail_price);
        mStockDetailPrice.setText("Updating ..");

        
        mStockDetailChange = (TextView)findViewById(R.id.stock_detail_change);
        mStockDetailChange.setText("Updating ..");
        
        
        mStockDailyHigh = (TextView)findViewById(R.id.stock_detail_daily_high);
        mStockDailyHigh.setText("Updating ..");
       
        mStockDailyLow = (TextView)findViewById(R.id.stock_detail_daily_low);
        mStockDailyLow.setText("Updating");
        
        
        mStockDailyVolume = (TextView)findViewById(R.id.stock_detail_volume);
        mStockDailyVolume.setText("Updating ..");
        
        
        mStockLastTradeTime = (TextView)findViewById(R.id.stock_detail_last_trade_time);
        mStockLastTradeTime.setText("Updating ..");

        
        mImage.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		
        		Intent intent = new Intent(v.getContext(),GraphDuration.class);          	
            	startActivityForResult(intent, GraphDuration.CHOOSE_TIMEFRAME);
            	
       		}            	
		});
        
        mGraphTimeframesRow.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		
        		Intent intent = new Intent(v.getContext(),GraphDuration.class);          	
            	startActivityForResult(intent, GraphDuration.CHOOSE_TIMEFRAME);
            	
       		}            	
		});

        
        mCompanyNewsExpandable = (TableRow)findViewById(R.id.expandable_company_news);
        mCompanyNewsArrow = (ImageView)findViewById(R.id.arrow_company_news);
        
        mYouTubeExpandable = (TableRow)findViewById(R.id.expandable_youtube);
        mYouTubeArrow = (ImageView)findViewById(R.id.arrow_youtube);
       
        if(mCompanyNewsToggleExpanded){
        	mCompanyNewsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        	mCompanyNewsExpandable.setVisibility(View.VISIBLE);
        }
        else{
        	mCompanyNewsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        	mCompanyNewsExpandable.setVisibility(View.GONE);
        }
        
        if(mYouTubeToggleExpanded){
        	mYouTubeArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        	mYouTubeExpandable.setVisibility(View.VISIBLE);
        }
        else{
        	mYouTubeArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        	mYouTubeExpandable.setVisibility(View.GONE);
        }

        
        TableRow companyNewsHeader = (TableRow)findViewById(R.id.click_company_news);
        companyNewsHeader.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		
        		if(mCompanyNewsToggleExpanded){
        			mCompanyNewsExpandable.setVisibility(View.GONE);
        			mCompanyNewsToggleExpanded = false;
        			mCompanyNewsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        		}
        		else{
        			mCompanyNewsExpandable.setVisibility(View.VISIBLE);
        			mCompanyNewsToggleExpanded = true;
        			mCompanyNewsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        		}
       		}      
        	
        });
        
        
        TableRow YouTubeHeader = (TableRow)findViewById(R.id.click_youtube);
        YouTubeHeader.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		
        		if(mYouTubeToggleExpanded){
        			mYouTubeExpandable.setVisibility(View.GONE);
        			mYouTubeToggleExpanded = false;
        			mYouTubeArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        		}
        		else{
        			mYouTubeExpandable.setVisibility(View.VISIBLE);
        			mYouTubeToggleExpanded = true;
        			mYouTubeArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        		}
       		}      
        	
        });
           
     
        /*
    
        
        try{
        	
        	
        	/*
	    	 // Set up GoogleAdView.
	         GoogleAdView adView = (GoogleAdView) findViewById(R.id.adview_markets_page);
	         AdSenseSpec adSenseSpec =
	             new AdSenseSpec(CLIENT_ID)     // Specify client ID. (Required)
	             .setCompanyName(COMPANY_NAME)  // Set company name. (Required)
	             .setAppName(APP_NAME)          // Set application name. (Required)
	             .setKeywords(mKeywords)         // Specify keywords.
	             .setChannel(CHANNEL_ID)        // Set channel ID.
	             .setAdType(AdType.TEXT)        // Set ad type to Text.
	             .setAdTestEnabled(false);       // Keep true while testing
	         	 
	         
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
      	
        	TableLayout layout = (TableLayout) findViewById(R.id.top_text_view_market); 
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

	         
	    	 }catch(Exception e){
	    		 Log.e(getLocalClassName(),"Error Retriving Google Ad");
	    	 }
	    */	 
     
        mTracker.dispatch();   
    }
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        
        
        case SHOW_ADDED_TO_PORTFOLIO:
            
	        return new AlertDialog.Builder(this)
	        .setTitle(R.string.add_index_to_portfolio_title)
	        .setMessage(R.string.add_to_portfolio_message)
	        .setIcon(android.R.drawable.ic_menu_add)
	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	//finish();
	            }
	        })             
	        .create();
	        
        
        case ALERT_DIALOG_USER_AGREEMENT:
            
            return new AlertDialog.Builder(this)
                .setTitle(R.string.end_user_agreement_title)
                .setMessage(R.string.end_user_agreement_text)
                .setCancelable(false)
                .setNeutralButton("EULA", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    	
                    	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.pocketools.com/eula"))); 
                    	finish();
                    	
                    }
                })
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    	if(!mDb.getDatabase().isOpen())
    	        			mDb.open();
    	        		
    	        		mDb.setUserAgreement("true");
    	        		
    	        		
    	        		//Let's show the splash screen
    	        		mDb.updateSplashScreenVersion(mSplashScreenVersion);
        				
        				Intent intent = new Intent(getApplicationContext(),FAQ.class);
                        startActivity(intent);
                    }
                })             
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    	finish();
                    }
                })
                
                
                .create();
          
          
        
   
        default:
       	 return null;
        
        }
    }
    
    public class ImageAdapterMarkets extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;

        private Integer[] mImageIds = {
        		R.drawable.currencies_off,
        		R.drawable.commodities_off,
        		R.drawable.markets_on,
                R.drawable.portfolio_off,
                R.drawable.alerts_off
        };

        
        
        public ImageAdapterMarkets(Context c) {
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
    
    
    private class DownloadDeals extends AsyncTask {
		
	     public Object doInBackground(Object... urls) { 
	    	 
	    	 /*
	    	 String locations = URLFetcher.getString("https://api.groupon.com/v2/divisions.json?client_id=1b275b4fea756d0e9cd2ebb4f2fba9a42df320d3");
	    	 
	    	 try{
	    	 JSONObject object = new JSONObject(locations);
	    	 

		Array array = object.getJSONArray("divisions");
	    	 
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
	    	     //  	Log.d("Stock Alert","About to read the JSON object ");
	    		 
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
				    			 
				    		//	 Log.d("Stock Alert","Now! Deal = " + dealObject.getString("isNowDeal"));
				    			 
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
			    	        ad.setBackgroundColor(Color.parseColor("#b2840a"));
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
		    	            	
		    	            //	Log.d("Stock Alert",CJ_AFFILIATE + deal.url);

		    	            	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CJ_AFFILIATE + deal.url)));          		
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
    
	private class DownloadImageTask extends AsyncTask {
		
	     public Object doInBackground(Object... urls) {
	    	 
	    	 //Log.d("PocketCurrency","DownloadImageTask");
	    	 
	    	 return URLFetcher.getBitmap((String)urls[0]); 
	     }

	     public void onPostExecute(Object result) {	    	 

	    	 
	    	 if(result != null){
	    		 
	    		 try{
	    		 
		    		 Bitmap mBitmap = (Bitmap)result;
		    				int picw  = mBitmap.getWidth();
		    				int pich = mBitmap.getHeight();
		    				
		    				int[] pix = new int[picw * pich];
		    				
		    				mBitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);
		    				boolean colorFound = false;
		    				boolean secondColorFound = false;
		    				
		    				for (int y = 0; y < pich; y++) {
		    					   for (int x = 0; x < picw; x++)
		    					      {
		    					      int index = y * picw + x;
		    					      
		    					      int r = (pix[index] >> 16) & 0xff;
		    					      int g = (pix[index] >> 8) & 0xff;
		    					      int b = pix[index] & 0xff;
		    					      	    					      
		    					      int rInv = r;
		    					      int gInv = g;
		    					      int bInv = b;
		    					      
		    					      if((g > 80 && g < 130) && (b > 125 && b < 180)){
		    					    	  
		    					    	  rInv = 255;
		    					    	  gInv = 255;
		    					    	  bInv = 255;
		    					      }
		    					      
		    					      else if((g > 225 && g < 245) && (b > 235 && b < 255)){
		    					    	  
		    					    	  //184,134,11
		    					    	  
		    					    	  rInv = 184;
		    					    	  gInv = 134;
		    					    	  bInv = 11;
		    					      }

		    					      else{
		    					    	  rInv = 255 - r;
		    					    	  gInv = 255 - g;
		    					    	  bInv = 255 - b;
		    					      }
		    					      
		    					      
		    					      pix[index] = 0xff000000 | (rInv << 16) | (gInv << 8) | bInv;
	
		    					      }
		    				}
		    				
		    				
		    					Bitmap bm = Bitmap.createBitmap(picw, pich, Bitmap.Config.RGB_565);
		    				    bm.setPixels(pix, 0, picw, 0, 0, picw, pich);
		    				    
		    				    mImage.setImageBitmap(bm);
		    				    
		    				    Display display = getWindowManager().getDefaultDisplay(); 
		    		            int width = display.getWidth();
		    		            int imageWidth = Math.round(new Float(width * .9));
		    		            int imageHeight = Math.round(new Float(imageWidth * .8));
		    		            
		    		            
		    				    mImage.setLayoutParams(new TableLayout.LayoutParams(imageWidth, imageHeight));
		    				    mImage.setScaleType(ImageView.ScaleType.FIT_XY);
		    				    
		    				    mChartDisplayed = true;
		    				    
		    				    if(mGraphAnimation != null)
		    				    	mGraphAnimation.stop();
		    				    
		    				    TableRow row = (TableRow)findViewById(R.id.graph_animation);
		    				    row.setVisibility(View.GONE);
			    		// mImage.setImageBitmap();    
			    	 }catch(Exception e){}
	    	 }
	     }
	}
	
	
	private class DownloadSingleStockTask extends AsyncTask {
		
	     public Object doInBackground(Object... urls) {
	    	 
	    	 //Log.d("PocketCurrency","DownloadImageTask");
	    	 
	    	 String result = URLFetcher.getString((String)urls[0]); 
	    	 return result;
	     }

	     public void onPostExecute(Object result) {	    	 

	    	 if(result != null){	
	    		 
	    		 
	    		 /*
	    		 if(!mDb.getDatabase().isOpen())
	    			 mDb.open();
				*/
	    		 
		    	 String resultString = (String)result;		    	 
		    	 
	    		 String ticker = "";
	    		 String rate = "0";
	    		 
	    		 //http://finance.yahoo.com/d/quotes.csv?s=MSFT&f=sxl1c1p2t1d1hgkjj1ab2bb3pot8va2ren
	    		 //In order of csv
	    		 //Company name goes at the end due to some companies having a "," in the name
	    		 String symbol = ""; //s
	    		 String stockExchange = ""; //x
	    		 String lastTrade = ""; //l1
	    		 String changeInPrice = ""; //c
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
		    	 
		    	// Log.d(getLocalClassName(),"RESPONSE FROM YAHOO = " + resultString);
		    	 
	
		   
		    	 try{
			    	 while(tokenizer.hasMoreTokens()){
			    		 String token = tokenizer.nextToken();	    		 
			    		 
			    		 StringTokenizer subtokenizer = new StringTokenizer(token,",");
			    		 
			    		 
			    		// HashMap<String, String>stockMap = new HashMap<String, String>();
			    		 
			    		 symbol = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("symbol", symbol);
			    		 
			    		 //Bug fix for DOW Jones
			    		 if(symbol.compareToIgnoreCase("INDU") ==0)
			    			 symbol = "^DJI";
			    		 
			    		 stockExchange = subtokenizer.nextToken().replaceAll("\"", "");
			    		 
			    		 
			    		//bug fix for NASDAQ stock exchange
			    		 if(stockExchange.startsWith("Nasdaq"))
			    			 stockExchange = "Nasdaq";
			    		 
			    		// stockMap.put("stock_exchange", stockExchange);
			    		 
			    		 lastTrade = subtokenizer.nextToken().replaceAll("\"", "");
			    		// stockMap.put("last_trade_price", lastTrade);
			    		 mStockDetailPrice.setText(lastTrade);
			    		 mPrice = lastTrade;
			    		 
			    		 changeInPrice = subtokenizer.nextToken().replaceAll("\"", "");
			    		// stockMap.put("change_in_price", changeInPrice);
			    		 
			    		 changeInPercentage = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("change_in_percentage", changeInPercentage);
			    		 mStockDetailChange.setText(changeInPrice + " " + changeInPercentage);
			    		 
			    		 if(changeInPrice.startsWith("-"))
			    			 mStockDetailChange.setTextColor(Color.RED);
			    		 
			    		 lastTradeTime = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("last_trade_time", lastTradeTime);
			    		 mStockLastTradeTime.setText(lastTradeTime);
			    		 
			    		 lastTradeDate = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("last_trade_date", lastTradeDate);
			    		 
			    		 dailyHigh = subtokenizer.nextToken().replaceAll("\"", "");	
			    		 //stockMap.put("daily_high", dailyHigh);
			    		 mStockDailyHigh.setText(dailyHigh);
			    		 
			    		 dailyLow = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("daily_low", dailyLow);
			    		 mStockDailyLow.setText(dailyLow);
			    		 
			    		 yearlyHigh = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("yearly_high", yearlyHigh);
		    		 
			    		 yearlyLow = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("yearly_low", yearlyLow);
			    		 /*
			    		 fiftyTwoWeekRange = subtokenizer.nextToken().replaceAll("\"", "");
			    		 stockMap.put("fifty_two_week_range", fiftyTwoWeekRange);
			    		 Log.d("StockValue","fiftyTwoWeekRange :" + fiftyTwoWeekRange);
			    		 */
			    		 marketCap = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("market_cap", marketCap);
			    		 
			    		 askPrice = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("ask_price", askPrice);
			    		 
			    		 askPriceRealtime = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("ask_price_realtime", askPriceRealtime);
			    		 
			    		 bidPrice = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("bid_price", bidPrice);
			    		 
			    		 bidPriceRealtime = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("bid_price_realtime", bidPriceRealtime);
			    		 
			    		 previousClosePrice = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("previous_close_price", previousClosePrice);
			    		 
			    		 openPrice = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("open_price", openPrice);
			    		 
			    		 one_year_target_price = subtokenizer.nextToken().replaceAll("\"", "");
			    		// stockMap.put("one_year_target_price", one_year_target_price);
			    		 
			    		 volume_average_3m = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("volume_average_3m", volume_average_3m);
			    		 
			    		 volume_average_daily = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("volume_average_daily", volume_average_daily);
			    		 mStockDailyVolume.setText(volume_average_daily);
			    		 
			    		 priceEarningsRatio = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("price_earnings_ratio", priceEarningsRatio);
			    		 
			    		 earningsPerShare = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("earnings_per_share", earningsPerShare);
			    		 
			    		 companyName = subtokenizer.nextToken().replaceAll("\"", "");
			    		 companyName = companyName.trim();
			    		 mStockDetailCompanyName.setText(companyName);
			    		 mCompanyName = companyName;
			    		// stockMap.put("company_name", companyName);
			    		 
			    		// mDb.updateStocks(symbol, stockMap);
			    		 
			    		 //mDb.updateRateDate(lastTradeDate + " " + lastTradeTime + " (EST)");
			    		 
			    		 
			    		 
			    		 
			    		//Bug fix for ^DJI since Yahoo API is flaky
				    	 Log.d("Stock Alert Pro", "Symbol = " + mSymbol);
				    	 
				    	 if(mSymbol.compareTo("^DJI")==0){
				    		 
				    		 String googleURL = "http://www.google.com/finance/info?q=" + mSymbol;
				    		 String jsonResult = URLFetcher.getString(googleURL);	    		 
				    		 String trimmedResult = jsonResult.replace("// ", "");
				    		 
				    		 if(jsonResult != null){
				    			 try{
				    				 
				    				 JSONArray array = new JSONArray(trimmedResult);
				    				 
				    				//Let's loop through first and fetch all thumbnails
						    		 for(int i=0;i< array.length();i++){
						    			 
						    			 
						    			 JSONObject element = array.getJSONObject(i);
						    			 symbol = element.getString("t");
						    			 String lastTradePrice = element.getString("l");
						    			 mStockDetailPrice.setText(lastTradePrice);
						    			 
						    			 changeInPercentage = element.getString("cp") + "%";
						    			 changeInPrice = element.getString("c");
						    			 
						    			 mStockDetailChange.setText(changeInPrice + " " + changeInPercentage);
						    			 if(changeInPrice.startsWith("-"))
						    				 mStockDetailChange.setTextColor(Color.RED);
						    			 else
						    				 mStockDetailChange.setTextColor(Color.GREEN);
						    			 
						    			 
						    			 lastTradeTime = element.getString("ltt");
						    			 mStockLastTradeTime.setText(lastTradeTime);
						    			 
						    			 
						    			 //Since our daily highs/lows will be incorrect, let's put them to ""
						    			 mStockDailyHigh.setText("");
						    			 mStockDailyLow.setText("");
						    			 
						    		 }
				    				 
				    				 

				    			 }catch (JSONException e){
				    				 Log.d(getLocalClassName(),"Exception thrown : " + e.getMessage());
				    			 }
				    		 }	 
				    	 }
			    		
			    		 mStillUpdating = false;
			    		 
			    	 }

			    		 
		    	 }catch(Exception e){}
	    	 } 
	     }
	}
	
	
	private class DownloadNewsTask extends AsyncTask {
		
		String mUrl = "";
		
	     public Object doInBackground(Object... urls) {
	    	 
	    	 return URLFetcher.getString((String)urls[0]); 
	     }

	     public void onPostExecute(Object result) {	 
	    	 
	    	 String jsonResult = (String)result;

	    	 
	    	 	mCompanyInfoDisplayed = true;
	    	 	
	    	 	if(mCompanyInfoAnimation != null)
	    	 		mCompanyInfoAnimation.stop();
			    
			    TableRow row = (TableRow)findViewById(R.id.company_info_animation);
			    row.setVisibility(View.GONE);
			    
			 
			 if(jsonResult == null) { //This means we did not get the result we expected. Let's try again
				 
				 String newURL = "http://www.google.com/finance/company_news?q=" + mSymbol + "&output=json&num=20"; 
				 jsonResult = (String)URLFetcher.getString(newURL);
				 
			 }
			 
			 
			 
			 if(jsonResult == null){ // If it's STILL null, then just go off the company name
				 String newURL = "http://www.google.com/finance/company_news?q=" + mCompanyNameStart + "&output=json&num=20"; 
				 jsonResult = (String)URLFetcher.getString(newURL);
			 }
			 
			    
	    	 
	    	 if(jsonResult != null){
	    		 
	    		// Log.d(getLocalClassName(), jsonResult);
    	 
	    		 
		    	 try{
		    		 
		    	 String trimmedResult = Utilities.searchForString("{clusters:", ",results_per_page", new StringBuffer(jsonResult));
		    	 String decodedResult = trimmedResult.replace("x26#39;", "'");
		    	 decodedResult = decodedResult.replace("x26amp;", "&");
		    	 
	    	 

		    		 
		    		// String decodedResult = java.net.URLDecoder.decode(trimmedResult,"UTF-8");

	    			 JSONArray array = new JSONArray(decodedResult);	    			 
		    		 
	    			 if(array.length() > 1){
		    		 
		    		 //Let's loop through first and fetch all thumbnails
		    		 for(int i=0;i< array.length();i++){
		    			 
		    			 
		    			 JSONObject element = array.getJSONObject(i);
		    			 String id = element.getString("id");
		    			 
		    			 if(id.compareTo("-1") !=0){
		  
			    			 JSONArray newsArray = element.getJSONArray("a");
			    			 
			    			 JSONObject newsObject = newsArray.getJSONObject(0);
			    			 
			    			 String title = newsObject.getString("t");

			    			 
			    			 String decodedTitle = title;
			    			 try{
			    				 decodedTitle = java.net.URLDecoder.decode(title,"UTF-8");
			    			 }catch(Exception e){}
			    			 
			    			 
			    			 String newsSource = newsObject.getString("s");
			    			 
			    			 /*
			    			
			    			 String decodedNewsSource = newsSource;
			    			 try{
			    				  decodedNewsSource = java.net.URLDecoder.decode(newsSource,"UTF-8");
			    			 }catch(Exception e){}
			    			 */
			    			 
			    			 String url = newsObject.getString("u");
			    			 
			    			 
			    			 String summary = newsObject.getString("sp");
			    			 
			    			 /*
			    			 String decodedSummary = summary;
			    			 
			    			 try{
			    				 decodedSummary = java.net.URLDecoder.decode(summary,"UTF-8");
			    			 }catch(Exception e){}
			    			 */
			    			 
			    			 
			    			 String publishDate = newsObject.getString("d");
 			
			    			
			    			TextView textTitle = new TextView(mContext);
			    			 textTitle.setTextColor(Color.WHITE);
			    			 textTitle.setTextSize(16);
			    			 textTitle.setText(decodedTitle);
			    			 textTitle.setPadding(5, 0, 0, 0);
			    			 
			    			 
			    			 textTitle.setClickable(true);
			    			 textTitle.setTag(url);
			    			 
			    			 textTitle.setOnClickListener(new OnClickListener() {
					            public void onClick(View v) {
					            	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String)v.getTag())));			              
						        }
						     });
			    			 
			    			 
			    			 mCompanyNewsTable.addView(textTitle);
			    			 
			    			 
			    			 TextView textSource = new TextView(mContext);
			    			 textSource.setTextColor(Color.GRAY);
			    			 textSource.setTextSize(14);
			    			 textSource.setText("( " + newsSource + " )");
			    			 textSource.setPadding(5, 0, 0, 5);
			    			 
			    			 mCompanyNewsTable.addView(textSource);
			    			 
			    			 
			    			 
			    			 
			    			 TextView textSummary = new TextView(mContext);
			    			 textSummary.setTextColor(Color.LTGRAY);
			    			 textSummary.setTextSize(14);
			    			 textSummary.setText(summary);
			    			 textSummary.setPadding(5, 0, 0, 0);
			    			 
			    			 
			    			 textSummary.setClickable(true);
			    			 textSummary.setTag(url);
			    			 
			    			 textSummary.setOnClickListener(new OnClickListener() {
					            public void onClick(View v) {
					            	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String)v.getTag())));			              
						        }
						     });
			    			 
			    			 
			    			 mCompanyNewsTable.addView(textSummary);
			    			 
			    			 
			    			 TextView publisherDate = new TextView(mContext);
			    			 publisherDate.setTextColor(Color.WHITE);
			    			 publisherDate.setTextSize(14);
			    			 publisherDate.setText(publishDate);
			    			 publisherDate.setPadding(5, 0, 0, 15);
			    			 /*
			    			 long unixDate = Long.parseLong(publishDate);
			    			 
			    			 Date date = new Date(unixDate * 1000);
			    			 publisherDate.setText(date.toLocaleString());
			    			 */
			    			 
			    			 mCompanyNewsTable.addView(publisherDate);
			    			 
			    			 
			    			 TextView breakView = new TextView(mContext);
			    			 breakView.setBackgroundColor(Color.LTGRAY);
			    			 breakView.setHeight(1);
			    			 
			    			 mCompanyNewsTable.addView(breakView);
			    			 
	    				 
		    			 }	
		    		 }
		    		 
		    		 }else{

			    			 TextView textTitle = new TextView(mContext);
			    			 textTitle.setTextColor(Color.WHITE);
			    			 textTitle.setTextSize(16);
			    			 textTitle.setText("No Market News Available");
			    			 textTitle.setPadding(5, 0, 0, 15);
			    			 
			    			 mCompanyNewsTable.addView(textTitle);	   

		    		 }
	    			 
		    	 }catch(Exception e){
		    	 }
		    	 
	    	 
	    	 }else{
	    		 
	    		 TextView textTitle = new TextView(mContext);
    			 textTitle.setTextColor(Color.WHITE);
    			 textTitle.setTextSize(16);
    			 textTitle.setText("No Company News Available");
    			 textTitle.setPadding(5, 0, 0, 15);
    			 
    			 mCompanyNewsTable.addView(textTitle);	    		 
	    	 }
	    	 
	    	 
	     }
	     
	     
	    	 
	     
	}
	private class DownloadYouTubeTask extends AsyncTask {
		
	     public Object doInBackground(Object... urls) {
  	 
	    	 String stringResult = (String)URLFetcher.getString((String)urls[0]); 
	    	 
	    	 mThumbnailCount = 0;
	    	 mYouTubeThumbnails = new HashMap<Integer, Bitmap>();
	    	 
	    	 try{
    			   			 
    			 String jsonResult = stringResult.substring(stringResult.indexOf("["),stringResult.indexOf("]}}")+1);
    			 
    			 JSONArray array = new JSONArray(jsonResult);
	    		 
	    		 
	    		 //Let's loop through first and fetch all thumbnails
	    		 for(int i=0;i< array.length();i++){
	    			 JSONObject element = array.getJSONObject(i); 
	    			 JSONObject thumbnailObject = element.getJSONObject("thumbnail");
	    			 String thumbnail = thumbnailObject.getString("sqDefault");
	    			 
	    			 //Bitmap bitmap = URLFetcher.getBitmap(thumbnail);		    			 
	    			 new DownloadYouTubeThumbnailTask().execute(new String[] {Integer.toString(i),thumbnail});
	    		 }

	    		 			 
    			 
    			 //Let's hold the show until we have all of our thumbnails back
    			 while(mThumbnailCount < array.length()){
    				 
    				 try{
    					 Thread.sleep(100);
    				 }
    				 catch(Exception e){
    					 throw new Exception(e);
    				 }
    				 
    			 }
    			 
	    	 }catch(Exception e){ return null;}
	    	 
	    	 return stringResult;
	    	 
	    	 
	     }

	     public void onPostExecute(Object result) {	
	    	 
	    	 
	    	 	mYouTubeDisplayed = true;
	    	 	
	    	 	if(mYouTubeAnimation != null)
	    	 		mYouTubeAnimation.stop();
			    
			    TableRow row = (TableRow)findViewById(R.id.youtube_animation);
			    row.setVisibility(View.GONE);
			    
	    	 
	    	 	if(result != null){
	    		 
	    		 try{
	    			 
	    			 String stringResult = (String)result;
	    			 
	    			 String jsonResult = stringResult.substring(stringResult.indexOf("["),stringResult.indexOf("]}}")+1);
		    		 JSONArray array = new JSONArray(jsonResult);		    		 

		    		 for(int i=0;i< array.length();i++){
		    			 JSONObject element = array.getJSONObject(i); 
		    			 
		    			 String publisher = element.getString("uploader");
		    			 String uploadDate = element.getString("uploaded");
		    			 JSONObject content = element.getJSONObject("content");
		    			 
		    			 
		    			 
		    			 
		    			 
		    			 // 1 - RTSP streaming URL for mobile video playback. H.263 video (up to 176x144) and AMR audio.
		    			 // 5 - HTTP URL to the embeddable player (SWF) for this video. This format is not available for a 
		    			 //     video that is not embeddable. Developers commonly add &format=5 to their queries to restrict 
		    			 //     results to videos that can be embedded on their sites.
		    			 // 6 - RTSP streaming URL for mobile video playback. MPEG-4 SP video (up to 176x144) and AAC audio.
		    			 
		    			 String url = content.getString(mVideoFormat);
		    			 String title = element.getString("title");
		    			 String description = element.getString("description");
		    			  
		    			 
		    			 
		    			
		    			 
		    			 TextView titleView = new TextView(mContext);
		    			 titleView.setText(title);
		    			 titleView.setTextSize(16);
		    			 titleView.setTextColor(Color.WHITE);
		    			 titleView.setPadding(5, 0, 0, 5);
		    			 mYouTubeNewsTable.addView(titleView);
		    			 
		    			 TextView publisherView = new TextView(mContext);
		    			 publisherView.setText("(" + publisher + ")");
		    			 publisherView.setTextSize(14);
		    			 publisherView.setTextColor(Color.GRAY);	
		    			 publisherView.setPadding(5, 0, 0, 5);
		    			 mYouTubeNewsTable.addView(publisherView);
		    			 
		    			 
		    			 ImageButton videoThumbnail = new ImageButton(mContext); 
		    			 videoThumbnail.setBackgroundDrawable(null);
		    			 videoThumbnail.setImageBitmap(mYouTubeThumbnails.get(i));
		    			 videoThumbnail.setTag(url);
		    			 
		    			 videoThumbnail.setOnClickListener(new Button.OnClickListener() {
		    		        	public void onClick(View v) {
		    		        		
		    		        		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String)v.getTag())));
		    		            	
		    		       		}            	
		    				});
		    			 
		    			 
		    			 mYouTubeNewsTable.addView(videoThumbnail);	    			 		    			 
		    			 
		    			 TextView descriptionView = new TextView(mContext);
		    			 descriptionView.setText(description);
		    			 descriptionView.setTextSize(14);
		    			 descriptionView.setTextColor(Color.WHITE);	
		    			 descriptionView.setPadding(5, 0, 0, 0);
		    			 descriptionView.setTag(url);
		    			 	    			
		    			 mYouTubeNewsTable.addView(descriptionView);
		    					    			 
		    			   			 
		    			 TextView uploadDateView = new TextView(mContext);
		    			 uploadDateView.setText(uploadDate);
		    			 uploadDateView.setTextSize(14);
		    			 uploadDateView.setTextColor(Color.WHITE);	
		    			 uploadDateView.setPadding(5, 0, 0, 15);
		    			 mYouTubeNewsTable.addView(uploadDateView);
		    			 
		    			 TextView breakView = new TextView(mContext);
		    			 breakView.setBackgroundColor(Color.LTGRAY);
		    			 breakView.setHeight(1);
		    			 
		    			 mYouTubeNewsTable.addView(breakView);	 
  			 
		    		 }

	    		
		    		 
	    		 
	    		 }catch(Exception e){
	    			 
	    			 TextView textTitle = new TextView(mContext);
	    			 textTitle.setTextColor(Color.WHITE);
	    			 textTitle.setTextSize(16);
	    			 textTitle.setText("No recent news found on YouTube.");
	    			 textTitle.setPadding(5, 0, 0, 15);
	    			 
	    			 mYouTubeNewsTable.addView(textTitle);	 
	    		 } 		 
	    	 }  
	    	 	
	    	 	else{ //Either Results are null or zero
		    		 
		    		 TextView textTitle = new TextView(mContext);
	    			 textTitle.setTextColor(Color.WHITE);
	    			 textTitle.setTextSize(16);
	    			 textTitle.setText("No recent news found on YouTube." );
	    			 textTitle.setPadding(5, 0, 0, 15);
	    			 
	    			 mYouTubeNewsTable.addView(textTitle);	    		 
		    	 }
	    	 
	    	 
	     }
	     
	}
	
	private class DownloadYouTubeThumbnailTask extends AsyncTask {
		
	     public Object doInBackground(Object... urls) {	 
	    	 
	    	 String position = (String)urls[0];
	    	 int positionInt = Integer.parseInt(position);

	    	 Bitmap thumbnail = URLFetcher.getBitmap((String)urls[1]); 
    	 
	    	 mYouTubeThumbnails.put(positionInt, thumbnail);
	    	 mThumbnailCount++;
	    	 
	    	 return null;
	     }

	     public void onPostExecute(Object result) {	
	    	 
	     }

	     
	}
	
	/*
	
	private class DownloadKeywordsTask extends AsyncTask {
		
		
    	public DBAdapter mThreadDB;
		
	     public Object doInBackground(Object... urls) {	 

	    	 String keywords = URLFetcher.getString((String)urls[0]); 

	    	 return keywords;
	     }
	     
	     public void onPreExecute(){
	    	 
	    	 mThreadDB = new DBAdapter(mContext);
	    	 mThreadDB.open();
	    	 
	     }
	     
	     public void onCancelled(){
	    	   	 
	    	 if(mThreadDB.getDatabase().isOpen())
	    		 mThreadDB.close();
	    	 
	    	 mThreadDB = null;
	     }

	     public void onPostExecute(Object result) {	
	    	 
	    	 if(!mThreadDB.getDatabase().isOpen())
    			 mThreadDB.open();
	    	 
	    	 String keywords = (String)result;
	    	 
	    	 if(keywords != null && keywords.length() > 0){
	    		 mKeywords = keywords.trim();	    				 

	    		 mThreadDB.updateGoogleKeywords(mKeywords);
	    		 Log.d(getLocalClassName(),"DOWNLOADED KEYWORDS '" + mKeywords + "'");
		 	     mThreadDB.close();
	 	     
	    	 }
	    	 
	    	 
	    	 try{
	    	 // Set up GoogleAdView.
	         GoogleAdView adView = (GoogleAdView) findViewById(R.id.adview_markets_page);
	         AdSenseSpec adSenseSpec =
	             new AdSenseSpec(CLIENT_ID)     // Specify client ID. (Required)
	             .setCompanyName(COMPANY_NAME)  // Set company name. (Required)
	             .setAppName(APP_NAME)          // Set application name. (Required)
	             .setKeywords(mKeywords)         // Specify keywords.
	             .setChannel(CHANNEL_ID)        // Set channel ID.
	             .setAdType(AdType.TEXT)        // Set ad type to Text.
	             .setAdTestEnabled(true);       // Keep true while testing
	         	 
	         
	         adView.showAds(adSenseSpec);
	         
	    	 }catch(Exception e){
	    		 Log.e(getLocalClassName(),"Error Retriving Google Ad");
	    	 }
	         

	    	 
	    	 
	     }

	     
	}
	
	*/

	
	
	@Override
    public void onWindowFocusChanged(boolean hasFocus){
		
 	
    	if(hasFocus){ 		
    		if(mNetworkInfo != null && mChartDisplayed == false){
	        	mGraphLoading = (ImageView)findViewById(R.id.graph_loading);
	        	mGraphLoading.setBackgroundResource(R.anim.spin_animation);
	        	mGraphLoading.setMaxHeight(40);
	        	mGraphLoading.setMaxWidth(40);
	        	
	        	// Get the background, which has been compiled to an AnimationDrawable object.
	        	 mGraphAnimation = (AnimationDrawable) mGraphLoading.getBackground();
	
	        	 // Start the animation (looped playback by default).
	        	 mGraphAnimation.start();	        	 
    		}
    		
    		if(mNetworkInfo != null && mCompanyInfoDisplayed == false){
	        	mCompanyInfoLoading = (ImageView)findViewById(R.id.company_info_loading);
	        	mCompanyInfoLoading.setBackgroundResource(R.anim.spin_animation);
	        	mCompanyInfoLoading.setMaxHeight(40);
	        	mCompanyInfoLoading.setMaxWidth(40);
	        	
	        	// Get the background, which has been compiled to an AnimationDrawable object.
	        	 mCompanyInfoAnimation = (AnimationDrawable) mCompanyInfoLoading.getBackground();
	
	        	 // Start the animation (looped playback by default).
	        	 mCompanyInfoAnimation.start();	        	 
    		}
    		
    		
    		if(mNetworkInfo != null && mYouTubeDisplayed == false){
	        	mYouTubeLoading = (ImageView)findViewById(R.id.youtube_loading);
	        	mYouTubeLoading.setBackgroundResource(R.anim.spin_animation);
	        	mYouTubeLoading.setMaxHeight(40);
	        	mYouTubeLoading.setMaxWidth(40);
	        	
	        	// Get the background, which has been compiled to an AnimationDrawable object.
	        	 mYouTubeAnimation = (AnimationDrawable) mYouTubeLoading.getBackground();
	
	        	 // Start the animation (looped playback by default).
	        	 mYouTubeAnimation.start();	        	 
    		}
    		
    		
    		
    	}
    	
    	super.onWindowFocusChanged(hasFocus);
    }
	
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
	    	
	 	
	    	menu.add(0,MENU_REFRESH,0,"Refresh")
	 		.setIcon(android.R.drawable.ic_menu_rotate);
	    	
	    	menu.add(0,MENU_ADS,0,"Ads Preferences Manager")
	 		.setIcon(android.R.drawable.ic_menu_edit);
	    	
	    	menu.add(0,MENU_CHANGE_INDEX,0,"Change Index")
	 		.setIcon(android.R.drawable.ic_menu_more);
	    	
	    	menu.add(0,MENU_ADD_TO_PORTFOLIO,0,"Add to Portfolio")
	 		.setIcon(android.R.drawable.ic_menu_add);
	    	
	    	menu.add(0,MENU_INFO,0,"Info")
	 		.setIcon(android.R.drawable.ic_menu_help);

	    	menu.add(0,MENU_SETTINGS,0,"Settings")
	 		.setIcon(android.R.drawable.ic_menu_edit);
	    	
    	  return result;

    }
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {   
        
        
	    case MENU_REFRESH:
	    	
	    	Intent intent = new Intent (this,Markets.class);
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
			
	    case MENU_CHANGE_INDEX:
	    	
	    	intent = new Intent (this,ChooseIndex.class);
			startActivity(intent); 
			
			finish();
			return true; 
			
	    	case MENU_ADD_TO_PORTFOLIO:      

            DBAdapter db = new DBAdapter(this);
            db.open();

            db.addStock(mSymbol, mCompanyName,mPrice);	                    
            db.close();
            
           showDialog(SHOW_ADDED_TO_PORTFOLIO);

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
        
        
        /*
        if(mKeywordsTask != null && mKeywordsTask.getStatus() == AsyncTask.Status.RUNNING)
        	mKeywordsTask.cancel(true);
    	*/
        
        if(mDownloadNewsTask != null && mDownloadNewsTask.getStatus() == AsyncTask.Status.RUNNING)
        	mDownloadNewsTask.cancel(true);
        
        if(mSingleStockTask != null && mSingleStockTask.getStatus() == AsyncTask.Status.RUNNING)
        	mSingleStockTask.cancel(true);
        
        if(mDownloadYouTubeTask != null && mDownloadYouTubeTask.getStatus() == AsyncTask.Status.RUNNING)
        	mDownloadYouTubeTask.cancel(true);
        
        
        if(!mDb.getDatabase().isOpen())
        	mDb.open();
        
        if(mCompanyNewsToggleExpanded)
        	mDb.updateDefaultValue("company_news_section_expanded", "on");
        else
        	mDb.updateDefaultValue("company_news_section_expanded", "off");
        
        if(mYouTubeToggleExpanded)
        	mDb.updateDefaultValue("youtube_section_expanded", "on");
        else
        	mDb.updateDefaultValue("youtube_section_expanded", "off");

        
        if(mDb.getDatabase().isOpen())
        	mDb.close();
        
       
        
  
        super.onStop();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
    	switch (requestCode) {
    	
	    	case GraphDuration.CHOOSE_TIMEFRAME:
	    		
	    		if(resultCode == GraphDuration.CHOOSE_TIMEFRAME_SUCCESS){
		    		String timeframe = data.getExtras().getString("TIMEFRAME");
		    		Intent intent = new Intent(this,GraphMarket.class);
	        		intent.putExtra("TIMEFRAME", timeframe);
	        		intent.putExtra("SYMBOL", mSymbol);
	      		
	        		startActivity(intent);
	    		}
		    	
		    	
                
              default:
            	  break;
        }
    }
    
    @Override
    protected void onDestroy() {
        Log.d(this.getLocalClassName(), "onDestroy()");
        
        if(mDb.getDatabase().isOpen())
        	mDb.close();
        
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
    	
    	if(!mDb.getDatabase().isOpen())
    		mDb.open();
	
        super.onStart();  
}
}