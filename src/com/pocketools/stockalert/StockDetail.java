package com.pocketools.stockalert;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.ads.*;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.pocketools.stockalert.R;
import com.pocketools.stockalert.service.OnAlarmReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.MultiAutoCompleteTextView.Tokenizer;





public class StockDetail extends Activity {
    /** Called when the activity is first created. */

	private DBAdapter mDb;
	private ImageButton mImage;
	private TableRow mGraphTimeframesRow;
	private TableRow mStatisticsExpandable;
	private TableRow mStockAlertsExpandable;
	private TableRow mCompanyNewsExpandable;
	private TableRow mYouTubeExpandable;
	//private TableRow mGraphsExpandable;
	private boolean mKeyStatisticsToggleExpanded = false;
	private boolean mStockAlertsToggleExpanded = false;
	private boolean mCompanyNewsToggleExpanded = false;
	private boolean mYouTubeToggleExpanded = false;
	private String mDefaultTimeframe = "1d";
	//private boolean mGraphsToggleExpanded = false;
	private ImageView mKeyStatisticsArrow;
	private ImageView mStockAlertsArrow;
	private ImageView mPortfolioValueArrow;
	private ImageView mCompanyNewsArrow;
	private ImageView mYouTubeArrow;
	//private ImageView mGraphsArrow;
	private CheckBox mHighAlertCheckbox;
	private CheckBox mLowAlertCheckbox;
	private String mSymbol = "";
	private String mExchange = "";
	private String mCompanyNameStart = "";
	private EditText mHighAlertPrice;
	private EditText mLowAlertPrice;
	private TextView mAlertHighText;
	private TextView mAlertLowText;
	private TableRow mAlertMessageRow;
	private String mPrice;
	private static String YAHOO_ID = ".LRljOPV34HotkuMkLW2IRCXRn5UvSoUQscvtgmwA550PVIEAOQm7awmxCoifyiq6TYmUMx8aQbXOg--";
	private TableLayout mCompanyNewsTable;
	private TableLayout mYouTubeNewsTable;
	private Context mContext;
	private boolean mStillUpdating = false;
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
	private NetworkInfo mNetworkInfo = null;
	private ImageView mGraphLoading;
	private ImageView mCompanyInfoLoading;
	private ImageView mYouTubeLoading;
	private HashMap<Integer,Bitmap> mYouTubeThumbnails;
	private String mVideoFormat = "5";
	private int mThumbnailCount = 0;
	private GoogleAnalyticsTracker mTracker;
	private String mDefaultLocation = "Current Location";
	
    public static final int DIALOG_HIGH_ALERT_NOT_SAVED = 1000;
    public static final int DIALOG_LOW_ALERT_NOT_SAVED = 1001;
    public static final int DIALOG_STOCK_STILL_UPDATING = 1003;
    public static final int MENU_HOME = 1003;
    
    
	// Replace with your own AdSense client ID.
    private static final String CLIENT_ID = "ca-mb-app-pub-1805290976571198";

    // Replace with your own company name.
    private static final String COMPANY_NAME = "DELADS";

    // Replace with your own application name.
    private static final String APP_NAME = "Stock Alert";

    // Replace with your own keywords used to target Google ad.
    // Join multiple words in a phrase with '+' and join multiple phrases with ','.
    private String mKeywords = "trading online";

    // Replace with your own AdSense channel ID.
    private static final String CHANNEL_ID = "5808897842";
	
	
    private String mAdPreferences = "";

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mTracker = GoogleAnalyticsTracker.getInstance();        
        // Start the tracker in manual dispatch mode...
        mTracker.startNewSession(StockAlert.ANALYTICS_UA_NUMBER, this);
        mTracker.trackPageView("/StockDetail");
        
        
        mContext = this;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        setContentView(com.pocketools.stockalert.R.layout.stock_detail); 
      
        if(this.getIntent().hasExtra("SYMBOL"))
        		mSymbol = this.getIntent().getStringExtra("SYMBOL");
        
        

        Uri uri = this.getIntent().getData();
        if(uri != null){
        	mSymbol =  uri.getFragment();
        	mStillUpdating = true;
        }
        
       // Log.d(getLocalClassName(),"SYMBOL = " + mSymbol);
        
        
        if(this.getIntent().hasExtra("STOCK_EXCHANGE"))
        mExchange = this.getIntent().getExtras().getString("STOCK_EXCHANGE");
        
             
        String companyNameTemp = "";
        
        if(this.getIntent().hasExtra("COMPANY_NAME_START"))
        	this.getIntent().getExtras().getString("COMPANY_NAME_START");

        try{
	        StringTokenizer tokenizer = new StringTokenizer(companyNameTemp," ");
			if(tokenizer.hasMoreTokens()){
				mCompanyNameStart = tokenizer.nextToken();
			}
        }catch(NullPointerException e){}
        
        
        
        
        mDb = new DBAdapter(this);
        mDb.open();
        
        boolean hasDefaultLocationSet = false;
        boolean hasAdPreferencesSet = false;
        

        Cursor defaults = mDb.getDefaultValues();
        startManagingCursor(defaults);
        
        for(int i=0; i< defaults.getCount(); i++){
        	defaults.moveToPosition(i);
        	
        	String default_type = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_TYPE)); 

        	if(default_type.compareTo("stock_alerts_section_expanded") ==0){
        		String graphExpanded = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		if(graphExpanded.compareTo("on")==0)
        			mStockAlertsToggleExpanded = true;
        		
        		else
        			mStockAlertsToggleExpanded = false;
        	}

        	
        	else if(default_type.compareTo("key_statistics_section_expanded") ==0){
        		String graphExpanded = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        		if(graphExpanded.compareTo("on")==0)
        			mKeyStatisticsToggleExpanded = true;
        		
        		else
        			mKeyStatisticsToggleExpanded = false;
        	}
        	
        	else if(default_type.compareTo("company_news_section_expanded") ==0){
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
        	/*
        	else if(default_type.compareTo("google_keywords") ==0){
        		mKeywords = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	}
        	*/
        	
        	else if(default_type.compareTo("default_timeframe") ==0){
        		mDefaultTimeframe = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
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
        
        if(!hasDefaultLocationSet){
        	mDb.addDefaultLocation(mDefaultLocation);
        }
        
        if(!hasAdPreferencesSet){
        	mDb.addAdPreferences(mAdPreferences);
        }
        else
        	Log.d("Stock Alert","Ads Preferences = " + mAdPreferences);
        
        
        
		
		mYouTubeThumbnails = new HashMap<Integer, Bitmap>();
        
        
		if(this.getIntent().hasExtra("STILL_UPDATING"))
			mStillUpdating = this.getIntent().getExtras().getBoolean("STILL_UPDATING");
        
		ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = serviceConn.getActiveNetworkInfo();
	    
        
        /*
         * Moving this down to the end so we're not going to have a conflict with the Database
         
        	if(mStillUpdating){
	        	String url = "http://finance.yahoo.com/d/quotes.csv?s=" + mSymbol + "&f=sxl1c1p2t1d1hgkjj1ab2bb3pot8va2ren";
	        	new DownloadSingleStockTask().execute(url);
        	}
        */
        
        
        mImage = (ImageButton)findViewById(com.pocketools.stockalert.R.id.graph_small);
        mGraphTimeframesRow = (TableRow)findViewById(R.id.graph_timeframes_row_stock_detail);
        
        
        TextView textView = (TextView)findViewById(R.id.detail_timeframe_1d);      
        if(mDefaultTimeframe.compareTo("1d") == 0)  	  
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        
        textView = (TextView)findViewById(R.id.detail_timeframe_5d);
        if(mDefaultTimeframe.compareTo("5d") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        textView = (TextView)findViewById(R.id.detail_timeframe_3m);
        if(mDefaultTimeframe.compareTo("3m") == 0)
      	  textView.setTextColor(Color.YELLOW);   	 
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        textView = (TextView)findViewById(R.id.detail_timeframe_1y);
        if(mDefaultTimeframe.compareTo("1y") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        textView = (TextView)findViewById(R.id.detail_timeframe_2y);
        if(mDefaultTimeframe.compareTo("2y") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        
        textView = (TextView)findViewById(R.id.detail_timeframe_5y);
        if(mDefaultTimeframe.compareTo("5y") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        
        
        
        if(mNetworkInfo != null){
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
        
        
        new DownloadNewsTask().execute(url);
        
        
        mYouTubeNewsTable = (TableLayout)findViewById(com.pocketools.stockalert.R.id.youtube_table);
        
        
        url = "http://gdata.youtube.com/feeds/api/videos?q=" +
        mCompanyNameStart + 
        "+company&orderby=relevance&start-index=1&max-results=10&v=2&alt=jsonc&category=News&time=this_week&safeSearch=strict&format=" + mVideoFormat;
       
       
        
        new DownloadYouTubeTask().execute(url);
        
        }  // if(mNetworkInfo != null){
        else{
        	Log.d(getLocalClassName(),"Connection Error");
	    	Toast.makeText(this, "Connection Error. Please check your connections and try again.", Toast.LENGTH_SHORT).show();
        }	
        

        
      
        Cursor stockDetail = mDb.getStockDetail(mSymbol);
        startManagingCursor(stockDetail);
        stockDetail.moveToFirst();
        
        mPrice = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_LAST_TRADE_PRICE));
        String companyName = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_COMPANY_NAME));
        String changeInPrice = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_CHANGE_IN_PRICE));
        String changeInPercentage = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_CHANGE_IN_PERCENTAGE));
        
        String dailyHigh = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_DAILY_HIGH));
        String dailyLow = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_DAILY_LOW));
        String dailyVolume = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_VOLUME_DAY));
        String lastTradeTime = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_LAST_TRADE_TIME));
        String yearlyHigh = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_52_WEEK_HIGH));
        String yearlyLow = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_52_WEEK_LOW));
        String PERatio = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_PRICE_EARNINGS_RATIO));
        String EPS = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_EARNINGS_PER_SHARE));
        
        String marketCap = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_MARKET_CAP));
        String oneYearTarget = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_1_YEAR_TARGET_PRICE));
        String averageVolume = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_VOLUME_AVERAGE_3M));
        String alertMessage = (String)stockDetail.getString(stockDetail.getColumnIndex(DBAdapter.KEY_ALERT_MESSAGE));
      
        
        stockDetail.close();
        
        mAlertMessageRow = (TableRow)findViewById(R.id.stock_detail_alert_message_row);
        
        //If we have an alert message , then show across the top
        if(alertMessage == null || alertMessage.startsWith("N/A")){       	
        	mAlertMessageRow.setVisibility(View.GONE);
        }
        else{
        	mAlertMessageRow.setVisibility(View.VISIBLE);
        	TextView message = (TextView)findViewById(R.id.stock_detail_alert_message);
        	message.setText(alertMessage);
        	
        	Button dismissButton = (Button)findViewById(R.id.stock_detail_alert_dismiss_button);
        	dismissButton.setOnClickListener(new Button.OnClickListener() {
            	public void onClick(View v) {
            		
            		mAlertMessageRow.setVisibility(View.GONE);
                	mDb.removeAlertMessage(mSymbol);
           		}            	
    		});
        	
        }
        
        
        
        TextView view = (TextView)findViewById(R.id.stock_detail_company_name);
        view.setText(companyName);
        
        mStockDetailPrice = (TextView)findViewById(R.id.stock_detail_price);
        
        if(mStillUpdating)
        	mStockDetailPrice.setText("Updating ..");
        else
        	mStockDetailPrice.setText(mPrice);
        
        mStockDetailChange = (TextView)findViewById(R.id.stock_detail_change);
        
        if(mStillUpdating)
        	mStockDetailChange.setText("Updating ..");
        else
        	mStockDetailChange.setText(changeInPrice + " " + changeInPercentage);
        
        
        if(changeInPrice != null &&  mStillUpdating){
        	
	        if(changeInPrice.startsWith("+"))
	        	mStockDetailChange.setTextColor(Color.GREEN);
	        else
	        	mStockDetailChange.setTextColor(Color.RED);
	        
        }
        
        
        mStockDailyHigh = (TextView)findViewById(R.id.stock_detail_daily_high);
        if(mStillUpdating)
        	mStockDailyHigh.setText("Updating ..");
        else
        	mStockDailyHigh.setText(dailyHigh);
        
        mStockDailyLow = (TextView)findViewById(R.id.stock_detail_daily_low);
        if(mStillUpdating)
        	mStockDailyLow.setText("Updating");
        else
        	mStockDailyLow.setText(dailyLow);
        
        mStockDailyVolume = (TextView)findViewById(R.id.stock_detail_volume);
        if(mStillUpdating)
        	mStockDailyVolume.setText("Updating ..");
        else
        	mStockDailyVolume.setText(dailyVolume);
        
        mStockLastTradeTime = (TextView)findViewById(R.id.stock_detail_last_trade_time);
        if(mStillUpdating)
        	mStockLastTradeTime.setText("Updating ..");
        else
        	mStockLastTradeTime.setText(lastTradeTime + " (EST)");
        
        
        view = (TextView)findViewById(R.id.stock_detail_52_week_high);
        view.setText(yearlyHigh);
        
        view = (TextView)findViewById(R.id.stock_detail_52_week_low);
        view.setText(yearlyLow);
        
        view = (TextView)findViewById(R.id.stock_detail_p_e_ratio);
        view.setText(PERatio);
        
        view = (TextView)findViewById(R.id.stock_detail_earnings_per_share);
        view.setText(EPS);
        
        view = (TextView)findViewById(R.id.stock_detail_1yr_target);
        view.setText(oneYearTarget);
        
        view = (TextView)findViewById(R.id.stock_detail_3m_volume);
        view.setText(averageVolume);
        
        view = (TextView)findViewById(R.id.stock_detail_market_cap);
        view.setText(marketCap);
        
        
        //StockUtil util = new StockUtil(mSymbol,mSymbol);
        //mImage.setTag(util);
        
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
        
        mStatisticsExpandable = (TableRow)findViewById(R.id.expandable_key_statistics);
        mKeyStatisticsArrow = (ImageView)findViewById(R.id.arrow_key_statistics);
        
        mStockAlertsExpandable = (TableRow)findViewById(R.id.expandable_stock_alerts);
        mStockAlertsArrow = (ImageView)findViewById(R.id.arrow_stock_alerts);

        
        mCompanyNewsExpandable = (TableRow)findViewById(R.id.expandable_company_news);
        mCompanyNewsArrow = (ImageView)findViewById(R.id.arrow_company_news);
        
        mYouTubeExpandable = (TableRow)findViewById(R.id.expandable_youtube);
        mYouTubeArrow = (ImageView)findViewById(R.id.arrow_youtube);
        
       // mGraphsExpandable = (TableRow)findViewById(R.id.expandable_graphs);
        //mGraphsArrow = (ImageView)findViewById(R.id.arrow_graphs);
        
        
        if(mKeyStatisticsToggleExpanded){
        	mKeyStatisticsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        	mStatisticsExpandable.setVisibility(View.VISIBLE);
        }
        else{
        	mKeyStatisticsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        	mStatisticsExpandable.setVisibility(View.GONE);
        }
        
        
        if(mStockAlertsToggleExpanded){
        	mStockAlertsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        	mStockAlertsExpandable.setVisibility(View.VISIBLE);
        }
        else{
        	mStockAlertsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        	mStockAlertsExpandable.setVisibility(View.GONE);
        }
        
       
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
        
        
        /*
        if(mGraphsToggleExpanded){
        	mGraphsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        	mGraphsExpandable.setVisibility(View.VISIBLE);
        }
        else{
        	mGraphsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        	mGraphsExpandable.setVisibility(View.GONE);
        }
        */
        
        
        
        TableRow keyStatsHeader = (TableRow)findViewById(R.id.click_key_statistics);
        keyStatsHeader.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		
        		if(mKeyStatisticsToggleExpanded){
        			mStatisticsExpandable.setVisibility(View.GONE);
        			mKeyStatisticsToggleExpanded = false;
        			mKeyStatisticsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        		}
        		else{
        			mStatisticsExpandable.setVisibility(View.VISIBLE);
        			mKeyStatisticsToggleExpanded = true;
        			mKeyStatisticsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        		}
       		}      
        	
        });
        
        
        TableRow stockAlertsHeader = (TableRow)findViewById(R.id.click_stock_alerts);
        stockAlertsHeader.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		
        		if(mStockAlertsToggleExpanded){
        			mStockAlertsExpandable.setVisibility(View.GONE);
        			mStockAlertsToggleExpanded = false;
        			mStockAlertsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        		}
        		else{
        			mStockAlertsExpandable.setVisibility(View.VISIBLE);
        			mStockAlertsToggleExpanded = true;
        			mStockAlertsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        		}
       		}      
        	
        });
        
        mHighAlertCheckbox = (CheckBox)findViewById(R.id.alert_high_checkbox);
        mLowAlertCheckbox = (CheckBox)findViewById(R.id.alert_low_checkbox);
        mHighAlertPrice = (EditText)findViewById(R.id.alert_high);
        mLowAlertPrice = (EditText)findViewById(R.id.alert_low);
        mAlertHighText = (TextView)findViewById(R.id.alert_high_text);
        mAlertLowText = (TextView)findViewById(R.id.alert_low_text);
        
        
        Cursor stockAlerts = mDb.getAlerts(mSymbol);
        
        startManagingCursor(stockAlerts);

        
        if(stockAlerts.getCount() > 0){
        	       	
        	stockAlerts.moveToFirst();
        	
        	Double highPrice = stockAlerts.getDouble((stockAlerts.getColumnIndex(DBAdapter.KEY_HIGH_ALERT_PRICE)));
        	mHighAlertPrice.setText(highPrice.toString());
        	
        	int highAlertisActive = stockAlerts.getInt((stockAlerts.getColumnIndex(DBAdapter.KEY_HIGH_ALERT_ACTIVE)));
        	if(highAlertisActive == 1){
        		mHighAlertCheckbox.setChecked(true);
        		mAlertHighText.setText("Active");
        		mAlertHighText.setTextColor(Color.GREEN);
        	}
        	else{
        		mHighAlertCheckbox.setChecked(false);
        		mAlertHighText.setText("Inactive");
        		mAlertHighText.setTextColor(Color.RED);
        	}
        	
        	
        	Double lowPrice = stockAlerts.getDouble((stockAlerts.getColumnIndex(DBAdapter.KEY_LOW_ALERT_PRICE)));
        	mLowAlertPrice.setText(lowPrice.toString());
        	
        	int lowAlertisActive = stockAlerts.getInt((stockAlerts.getColumnIndex(DBAdapter.KEY_LOW_ALERT_ACTIVE)));
        	if(lowAlertisActive == 1){
        		mLowAlertCheckbox.setChecked(true);
        		mAlertLowText.setText("Active");
        		mAlertLowText.setTextColor(Color.GREEN);
        	}
        	else{
        		mLowAlertCheckbox.setChecked(false); 
        		mAlertLowText.setText("Inactive");
        		mAlertLowText.setTextColor(Color.RED);
        	}
        }
        
      //If there's any change to the price, we disable the alerts
        mHighAlertPrice.setOnKeyListener(new EditText.OnKeyListener() {

        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		
        		
        		
        		if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU || event.getAction() == KeyEvent.ACTION_UP){
        			//do nothing
        		}
        		else{
	        		mHighAlertCheckbox.setChecked(false);
	        		mAlertHighText.setText("Inactive");
	        		mAlertHighText.setTextColor(Color.RED);
	        		mDb.activateHighAlert(mSymbol, false);
        		}
        		
        		return false;
        	}
        });
        
       
        
        //If there's any change to the price, we disable the alerts
        mLowAlertPrice.setOnKeyListener(new EditText.OnKeyListener() {

        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		
        		if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU){
        			//do nothing
        		}
        		else{
	        		mLowAlertCheckbox.setChecked(false);
	        		mAlertLowText.setText("Inactive");
	        		mAlertLowText.setTextColor(Color.RED);
	        		mDb.activateLowAlert(mSymbol, false);
        		}
        		
        		return false;
        	}
        });
          
        
        mHighAlertCheckbox.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
          	
                // Perform action on clicks
                if (mHighAlertCheckbox.isChecked()) {  
                	
                	if(mStillUpdating){
                		mHighAlertCheckbox.setChecked(false);
                		showDialog(DIALOG_STOCK_STILL_UPDATING);
                	}
                	else{
	                	//Let's hide the keyboard if it's still showing
	                	InputMethodManager imm = (InputMethodManager)getSystemService(v.getContext().INPUT_METHOD_SERVICE);
	                	imm.hideSoftInputFromWindow(mHighAlertPrice.getWindowToken(), 0);
	                	
	                	
	                	//Let's make sure the user has SOMETHING in here in the first place
	                	if(mHighAlertPrice.getText().toString().length() < 1)
	                		mHighAlertPrice.setText("0");
	                	
	                	//Let's check to make sure the high alert is higher than the current price
	                	if(Double.parseDouble(mHighAlertPrice.getText().toString()) > Double.parseDouble(mPrice)){
		
	                		mDb.activateHighAlert(mSymbol, true, mHighAlertPrice.getText().toString());
	                		mAlertHighText.setText("Active");
	                    	mAlertHighText.setTextColor(Color.GREEN);
	                    	
	                    	Toast.makeText(v.getContext(), "Alert activated at " + mHighAlertPrice.getText().toString(), Toast.LENGTH_SHORT).show();                    	
	                    	
	                    	Cursor alerts = null;
                    	    alerts = mDb.getActiveAlerts();
                    	    
                    	    //If we don't have any previous alerts, we need to start the Alerts System
                    	    if(alerts.getCount() == 1){
                    	    	
                    	    	AlarmManager mgr=(AlarmManager)v.getContext().getSystemService(Context.ALARM_SERVICE);
                    			Intent alertIntent=new Intent(v.getContext(), OnAlarmReceiver.class);
                    			PendingIntent alertPI=PendingIntent.getBroadcast(v.getContext(), 0,alertIntent, 0);
                    			
                    			//Let's make sure we are as reliable as possible
                    			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+10000,Markets.PERIOD,alertPI);
                    			
                    			
                    			Log.d("Stock Alert","Starting / Restarting the Alerts System");    
                    			
                    	    	
                    	    }
                    	    alerts.close();
	                	
	                	}
	                	
	                	else{                		
	                		mHighAlertCheckbox.setChecked(false);
	                		mAlertHighText.setText("Inactive");
	                    	mAlertHighText.setTextColor(Color.RED);
	                    	
	                    	showDialog(DIALOG_HIGH_ALERT_NOT_SAVED);
	                	}	      
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
            			
            			
            			Log.d("Stock Alert","Shutting down Alerts System");    
            			
            	    	
            	    }
            	    
            	    alerts.close();
                    
                }
            }
        });
        
        mLowAlertCheckbox.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
          	
                // Perform action on clicks
                if (mLowAlertCheckbox.isChecked()) { 
                	
                	if(mStillUpdating){
                		mLowAlertCheckbox.setChecked(false);
                		showDialog(DIALOG_STOCK_STILL_UPDATING);
                	}
                	else{
                	
	                	//Let's hide the keyboard if it's still showing
	                	InputMethodManager imm = (InputMethodManager)getSystemService(v.getContext().INPUT_METHOD_SERVICE);
	                	imm.hideSoftInputFromWindow(mLowAlertPrice.getWindowToken(), 0);
	                	
	                	
	                	//Let's make sure the user has SOMETHING in here in the first place
	                	if(mLowAlertPrice.getText().toString().length() < 1)
	                		mLowAlertPrice.setText("0");
	                	
	                	//Let's check to make sure the high alert is higher than the current price
	                	if(Double.parseDouble(mLowAlertPrice.getText().toString()) < Double.parseDouble(mPrice)){
	              		
	                		mDb.activateLowAlert(mSymbol, true, mLowAlertPrice.getText().toString());
	                		mAlertLowText.setText("Active");
	                    	mAlertLowText.setTextColor(Color.GREEN);
	                    	
	                    	Toast.makeText(v.getContext(), "Alert activated at " + mLowAlertPrice.getText().toString(), Toast.LENGTH_SHORT).show();                    	
	                	
	                    	Cursor alerts = null;
                    	    alerts = mDb.getActiveAlerts();
                    	    
                    	    //If we don't have any previous alerts, we need to start the Alerts System
                    	    if(alerts.getCount() == 1){
                    	    	
                    	    	AlarmManager mgr=(AlarmManager)v.getContext().getSystemService(Context.ALARM_SERVICE);
                    			Intent alertIntent=new Intent(v.getContext(), OnAlarmReceiver.class);
                    			PendingIntent alertPI=PendingIntent.getBroadcast(v.getContext(), 0,alertIntent, 0);
                    			
                    			//Let's make sure we are as reliable as possible
                    			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+10000,Markets.PERIOD,alertPI);
                    			
                    			
                    			Log.d("Stock Alert","Starting / Restarting the Alerts System");    
                    			
                    	    	
                    	    }
                    	    alerts.close();
	                	
	                	}
	                	
	                	else{
	                		mLowAlertCheckbox.setChecked(false);
	                		mAlertLowText.setText("Inactive");
	                    	mAlertLowText.setTextColor(Color.RED);
	                    	
	                    	showDialog(DIALOG_LOW_ALERT_NOT_SAVED);
	                	}
                	
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
            			
            			
            			Log.d("Stock Alert","Shutting down Alerts System");    
            			
            	    	
            	    }
            	    
            	    alerts.close();
                }
            }
        });
  

        
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
        
        
        if(mNetworkInfo != null && mStillUpdating){
        	
        	String symbol = mSymbol;
        	
        	//Fix for the DOW
        	if(mSymbol.compareToIgnoreCase("^DJI") == 0){
        		symbol = "INDU";
        	}
        	
	        String url = "http://finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=sxl1c1p2t1d1hgkjj1ab2bb3pot8va2ren";
	        new DownloadSingleStockTask().execute(url);
        }
        
        
        try{
        	
        	/*
	        // Set up GoogleAdView.
	        GoogleAdView adView = (GoogleAdView) findViewById(R.id.adview_details_page);
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
        	
        	TableLayout layout = (TableLayout) findViewById(R.id.top_text_view_details); 
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
        
        /*
        TableRow graphsHeader = (TableRow)findViewById(R.id.click_graphs);
        graphsHeader.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		
        		if(mGraphsToggleExpanded){
        			mGraphsExpandable.setVisibility(View.GONE);
        			mGraphsToggleExpanded = false;
        			mGraphsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        		}
        		else{
        			mGraphsExpandable.setVisibility(View.VISIBLE);
        			mGraphsToggleExpanded = true;
        			mGraphsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        		}
       		}      
        	
        });
        */
        
       
   
    }
    
    
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
		    	            	
		    	            //	Log.d("Stock Alert",CJ_AFFILIATE + deal.url);

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
		
		public DBAdapter mThreadDB;
		
	     public Object doInBackground(Object... urls) {
	    	 
	    	 //Log.d("PocketCurrency","DownloadImageTask");
	    	 
	    	 return URLFetcher.getString((String)urls[0]); 
	     }

	     public void onPostExecute(Object result) {	    	 

	    	 if(result != null){	    		 
	    		 if(!mThreadDB.getDatabase().isOpen())
	    			 mThreadDB.open();

	    		 
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
		    	 
		    	 
		    	 try{
			    	 while(tokenizer.hasMoreTokens()){
			    		 String token = tokenizer.nextToken();	    		 
			    		 
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
			    		 mStockDetailPrice.setText(lastTrade);
			    		 mPrice = lastTrade;
			    		 
			    		 changeInPrice = subtokenizer.nextToken().replaceAll("\"", "");
			    		 stockMap.put("change_in_price", changeInPrice);
			    		 
			    		 changeInPercentage = subtokenizer.nextToken().replaceAll("\"", "");	
			    		 stockMap.put("change_in_percentage", changeInPercentage);
			    		 mStockDetailChange.setText(changeInPrice + " " + changeInPercentage);
			    		 
			    		 if(changeInPrice.startsWith("-"))
			    			 mStockDetailChange.setTextColor(Color.RED);
			    		 
			    		 lastTradeTime = subtokenizer.nextToken().replaceAll("\"", "");	
			    		 stockMap.put("last_trade_time", lastTradeTime);
			    		 mStockLastTradeTime.setText(lastTradeTime);
			    		 
			    		 lastTradeDate = subtokenizer.nextToken().replaceAll("\"", "");	
			    		 stockMap.put("last_trade_date", lastTradeDate);
			    		 
			    		 dailyHigh = subtokenizer.nextToken().replaceAll("\"", "");	
			    		 stockMap.put("daily_high", dailyHigh);
			    		 mStockDailyHigh.setText(dailyHigh);
			    		 
			    		 dailyLow = subtokenizer.nextToken().replaceAll("\"", "");	
			    		 stockMap.put("daily_low", dailyLow);
			    		 mStockDailyLow.setText(dailyLow);
			    		 
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
			    		 mStockDailyVolume.setText(volume_average_daily);
			    		 
			    		 priceEarningsRatio = subtokenizer.nextToken().replaceAll("\"", "");	
			    		 stockMap.put("price_earnings_ratio", priceEarningsRatio);
			    		 
			    		 earningsPerShare = subtokenizer.nextToken().replaceAll("\"", "");	
			    		 stockMap.put("earnings_per_share", earningsPerShare);
			    		 
			    		 companyName = subtokenizer.nextToken().replaceAll("\"", "");
			    		 companyName = companyName.trim();
			    		 stockMap.put("company_name", companyName);
			    		 
			    		 mThreadDB.updateStocks(symbol, stockMap);
			    		 
			    		 //mDb.updateRateDate(lastTradeDate + " " + lastTradeTime + " (EST)");
			    		 
			    		 
			    		 
			    		 
			    		//Bug fix for ^DJI. We need to get this from Google as the Yahoo Finance API is too flaky
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
						    			 
						    			 //If it has a comma, lets remove
						    			 mPrice = lastTradePrice.replace(",", "");
						    			 mStockDetailPrice.setText(mPrice);
						    			 
						    			 changeInPercentage = element.getString("cp") + "%";
						    			 changeInPrice = element.getString("c");
						    			 
						    			 mStockDetailChange.setText(changeInPrice + " " + changeInPercentage);
						    			 if(changeInPrice.startsWith("-"))
						    				 mStockDetailChange.setTextColor(Color.RED);
						    			 else
						    				 mStockDetailChange.setTextColor(Color.GREEN);
						    			 
						    			 
						    			 lastTradeTime = element.getString("ltt");
						    			 mStockLastTradeTime.setText(lastTradeTime);
						    			 
						    			 lastTradeDate = element.getString("lt");
						    			 
						    			 mStockDailyHigh.setText("");
						    			 mStockDailyLow.setText("");
						    			 

						    			 mThreadDB.updateRealtimePrice(mSymbol, mPrice, changeInPercentage, changeInPrice, lastTradeTime, lastTradeDate);
						    			 
						    			 
						    		 }
				    				 
				    				 

				    			 }catch (JSONException e){
				    				 Log.d(getLocalClassName(),"Exception thrown : " + e.getMessage());
				    			 }
				    		 }	 
				    	 }
    		
				    	 
			    		
			    		 mStillUpdating = false;
			    		 
			    		 mThreadDB.close();
			    		 
			    	 }
		    	 }catch(NullPointerException e){}
	    	 } 
	     }
	     
	     public void onPreExecute(){
	    	 
	    	 mThreadDB = new DBAdapter(mContext);
	    	 mThreadDB.open();
	    	 
	     }
	     
	     public void onCancelled(){
	    	 mThreadDB.close();
	    	 mThreadDB = null;
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
    	 
	    		 
	    		 try{
		    	 String trimmedResult = Utilities.searchForString("{clusters:", ",results_per_page", new StringBuffer(jsonResult));
		    	 String decodedResult = trimmedResult.replace("x26#39;", "'");
		    	 decodedResult = decodedResult.replace("x26amp;", "&");
		    	 
	    	 
		    	 
		    		 
		    		// String decodedResult = java.net.URLDecoder.decode(trimmedResult,"UTF-8");

	    			 JSONArray array = new JSONArray(decodedResult);
		    		 
		    		 
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
	    	 
	    	 /*
	    	 
	    	 String numberOfResults = "0";
	    	 
	    	 if(result != null){	    		 
	    		 numberOfResults = Utilities.searchForString("totalResultsAvailable=\"", "\"", new StringBuffer((String)result));	    		 
	    		 //Let's download news for the company name if we don't have anything for the stock symbol
	    		 if(numberOfResults == null || numberOfResults.compareTo("0") == 0){
	    			 String url = "http://search.yahooapis.com/NewsSearchService/V1/newsSearch?appid=" + YAHOO_ID + "&query=" + 
	    			 mCompanyNameStart + "&type=any&results=20&language=en&sort=date";
		    		 result = URLFetcher.getString(url); 
		    		 
		    		 if(result != null)
		    			 try{
		    				 numberOfResults = Utilities.searchForString("totalResultsAvailable=\"", "\"", new StringBuffer((String)result));
		    			 }catch(Exception e){
		    				 //We don't have any results
		    				 numberOfResults = "0";
		    			 }
	    		 }
	    	 
	    	 }
	    	 
	    	 	mCompanyInfoDisplayed = true;
			    mCompanyInfoAnimation.stop();
			    
			    TableRow row = (TableRow)findViewById(R.id.company_info_animation);
			    row.setVisibility(View.GONE);

	    	 
	    	 if(result != null && numberOfResults.compareTo("0")!=0){
	    		 
	    		 try{
	    		 
	    		 StringTokenizer tokenizer = new StringTokenizer((String)result,"\n");
	    		 while(tokenizer.hasMoreTokens()){
	    			 
	    			
	    			 
	    			 String next = tokenizer.nextToken();
	    			 
	    			 if(next.startsWith("<Result>")){
	    			 	    			 
	    				 StringBuffer token = new StringBuffer(next);
	    				 
		    			 String title = Utilities.searchForString("<Title>", "</Title>", token);
		    			 String summary = Utilities.searchForString("<Summary>", "</Summary>", token);
		    			 String url = Utilities.searchForString("<Url>", "</Url>", token);
		    			 String newsSource = Utilities.searchForString("<NewsSource>", "</NewsSource>", token);
		    			 String publishDate = Utilities.searchForString("<PublishDate>", "</PublishDate>", token);
    			 
		    			 TextView textTitle = new TextView(mContext);
		    			 textTitle.setTextColor(Color.WHITE);
		    			 textTitle.setTextSize(16);
		    			 textTitle.setText(title);
		    			 textTitle.setPadding(5, 0, 0, 0);
		    			 
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
		    			 
		    			 long unixDate = Long.parseLong(publishDate);
		    			 
		    			 Date date = new Date(unixDate * 1000);
		    			 publisherDate.setText(date.toLocaleString());
		    			 
		    			 
		    			 mCompanyNewsTable.addView(publisherDate);
		    			 
		    			 
		    			 TextView breakView = new TextView(mContext);
		    			 breakView.setBackgroundColor(Color.LTGRAY);
		    			 breakView.setHeight(1);
		    			 
		    			 mCompanyNewsTable.addView(breakView);
		    			 
	    			 
	    			 }
	    			 	 
	    		 }
	    		 
	    		 }catch(Exception e){}     		
	    	 }
	    	 else{ //Either Results are null or zero
	    		 
	    		 TextView textTitle = new TextView(mContext);
    			 textTitle.setTextColor(Color.WHITE);
    			 textTitle.setTextSize(16);
    			 textTitle.setText("No Company News Available");
    			 textTitle.setPadding(5, 0, 0, 15);
    			 
    			 mCompanyNewsTable.addView(textTitle);	    		 
	    	 }
	    	 
	    	 
	    	 */
	    	 
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
        
        case DIALOG_STOCK_STILL_UPDATING:
            return new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_stock_still_updating_title)
                .setMessage(R.string.dialog_stock_still_updating_message)
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
        
        
        if(!mDb.getDatabase().isOpen())
        	mDb.open();
        
        if(mKeyStatisticsToggleExpanded)
        	mDb.updateDefaultValue("key_statistics_section_expanded", "on");
        else
        	mDb.updateDefaultValue("key_statistics_section_expanded", "off");
        
        if(mStockAlertsToggleExpanded)
        	mDb.updateDefaultValue("stock_alerts_section_expanded", "on");
        else
        	mDb.updateDefaultValue("stock_alerts_section_expanded", "off");
        
        
        if(mCompanyNewsToggleExpanded)
        	mDb.updateDefaultValue("company_news_section_expanded", "on");
        else
        	mDb.updateDefaultValue("company_news_section_expanded", "off");
        
        if(mYouTubeToggleExpanded)
        	mDb.updateDefaultValue("youtube_section_expanded", "on");
        else
        	mDb.updateDefaultValue("youtube_section_expanded", "off");
        
        /*
        if(mGraphsToggleExpanded)
        	mDb.updateDefaultValue("graph_section_expanded", "on");
        else
        	mDb.updateDefaultValue("graph_section_expanded", "off");
		*/
        
        if(mDb.getDatabase().isOpen())
        	mDb.close();
  
        super.onStop();
    }
    
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
	    	
	 	
	    	menu.add(0,MENU_HOME,0,"Home")
	 		.setIcon(R.drawable.menu_home);
	    	
    	  return result;

    }
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {   
        
        
	    case MENU_HOME:
	    	
	    	Intent intent = new Intent (this,Markets.class);
			startActivity(intent); 
			
			finish();
			return true; 
		
    }
        
        
        
        return false;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
    	switch (requestCode) {
    	
	    	case GraphDuration.CHOOSE_TIMEFRAME:
	    		
	    		if(resultCode == GraphDuration.CHOOSE_TIMEFRAME_SUCCESS){
		    		String timeframe = data.getExtras().getString("TIMEFRAME");
		    		Intent intent = new Intent(this,Graph.class);
	        		intent.putExtra("TIMEFRAME", timeframe);
	        		intent.putExtra("CURRENCY_LEFT", mSymbol);
	        		intent.putExtra("CURRENCY_RIGHT", mSymbol);
	      		
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