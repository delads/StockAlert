package com.pocketools.stockalert;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.ads.*;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.os.Vibrator;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Config;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.MultiAutoCompleteTextView.Tokenizer;



public class Currencies extends Activity {
    /** Called when the activity is first created. */

	private DBAdapter mDb;
	private ImageButton mImage;
	private TableRow mGraphTimeframesRow;
	private TableRow mCompanyNewsExpandable;
	private boolean mCompanyNewsToggleExpanded = true;
	private ImageView mCompanyNewsArrow;
	private String mCurrencyLeft = "";
	private String mCurrencyRight = "";
	private String mExchange = "";
	private String mDefaultTimeframe = "1d";
	private String mCompanyName="";
	private String mPrice;
	private TableLayout mCompanyNewsTable;
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
	private boolean mChartDisplayed = false;
	private boolean mCompanyInfoDisplayed = false;
	private NetworkInfo mNetworkInfo = null;
	private ImageView mGraphLoading;
	private ImageView mCompanyInfoLoading;
	private String mVideoFormat = "5";
	private GoogleAnalyticsTracker mTracker;
	 int mSpinnerLeftPosition = 0;
	    int mSpinnerRightPosition = 0;
		ArrayAdapter<String> mLeftAdapter;
		ArrayAdapter<String> mRightAdapter;
	    Spinner mLeftSpinner = null;
	    Spinner mRightSpinner = null;
	private static final int MENU_REFRESH = 1001;
	private static final int MENU_ADD_TO_PORTFOLIO = 1003;
	private static final int MENU_INFO = 1004;
	private static final int MENU_SETTINGS = 1006;
	private static final int SHOW_ADDED_TO_PORTFOLIO = 1005;
	
	
	// Replace with your own AdSense client ID.
    private static final String CLIENT_ID = "ca-mb-app-pub-1805290976571198";

    // Replace with your own company name.
    private static final String COMPANY_NAME = "DELADS";

    // Replace with your own application name.
    private static final String APP_NAME = "Stock Alert";

    // Replace with your own keywords used to target Google ad.
    // Join multiple words in a phrase with '+' and join multiple phrases with ','.
    private String mKeywords = "trading forex";

    // Replace with your own AdSense channel ID.
    private static final String CHANNEL_ID = "2379893596";
    
	
	
	

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mTracker = GoogleAnalyticsTracker.getInstance();        
        // Start the tracker in manual dispatch mode...
        mTracker.start(StockAlert.ANALYTICS_UA_NUMBER, this);
        mTracker.trackPageView("/Currencies");
        
        
        mContext = this;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        setContentView(com.pocketools.stockalert.R.layout.currencies); 
        
        
        
        
        mDb = new DBAdapter(this);
        mDb.open();
        
        
        Cursor defaultCleck = mDb.getDefaultValue("default_currency_left");
        startManagingCursor(defaultCleck);
        
        if(defaultCleck.getCount() < 1)
        	mDb.addDefaultPair("default_currency_left", "USD");
        
        defaultCleck = mDb.getDefaultValue("default_currency_right");
        startManagingCursor(defaultCleck);
        
        if(defaultCleck.getCount() < 1)
        	mDb.addDefaultPair("default_currency_right", "EUR");
        
        
        
        Cursor defaults = mDb.getDefaultValues();
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
        	
        	
        	else if(default_type.compareTo("default_currency_left") ==0){
        		mCurrencyLeft = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	}
        	
        	else if(default_type.compareTo("default_currency_right") ==0){
        		mCurrencyRight = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	}
        	
        	/*
        	else if(default_type.compareTo("google_keywords") ==0){
        		mKeywords = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	}
        	*/
        	
        	else if(default_type.compareTo("default_timeframe") ==0){
        		mDefaultTimeframe = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	}
        	
        }
        
       
        
        ImageButton swap = (ImageButton)findViewById(R.id.button_swap_currency);
        swap.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		
        		mDb.updateDefaultValue("default_currency_left", mCurrencyRight);
        		mDb.updateDefaultValue("default_currency_right", mCurrencyLeft);
        		
        		
        		Intent intent = new Intent(v.getContext(),Currencies.class);          	
            	startActivity(intent);
            	finish();
            	
       		}            	
		});
        
        
        
        
        
        for(int j=0; j< currencyArray.length; j++){
	      	
        	String longCurrency = currencyArray[j];
        	
        	String currency = longCurrency.substring(0,longCurrency.indexOf(" "));     	
        	
        	if(currency.compareTo(mCurrencyLeft) == 0)
        		mSpinnerLeftPosition = j;
        	
        	if(currency.compareTo(mCurrencyRight) == 0)
        		mSpinnerRightPosition = j;
        	
        }      
        
        
        mLeftSpinner = (Spinner) findViewById(R.id.currency_spinner_left);
        mLeftAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyArray);
        mLeftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLeftSpinner.setAdapter(mLeftAdapter);
        mLeftSpinner.setSelection(mSpinnerLeftPosition,false);
       
     // This listener is used to set the selected timeframe from the Spinner.
        mLeftSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View v, int position,
              long id) {
				String longCurrency = (String)mLeftAdapter.getItem(position);
				mCurrencyLeft = longCurrency.substring(0,longCurrency.indexOf(" "));
         	  	
				//mDb.getDatabase().setLockingEnabled(true);
          	  	mDb.updateDefaultValue("default_currency_left", mCurrencyLeft);       	 
          	  	
          	  	Intent intent = new Intent(v.getContext(),Currencies.class);
          	  	startActivity(intent);
          	  	finish();
     	  	
          }
	        public void onNothingSelected(AdapterView arg0) {
	          // NOP
	        }
	      });       
        mRightSpinner = (Spinner) findViewById(R.id.currency_spinner_right);
        mRightAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyArray);
        mRightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRightSpinner.setAdapter(mRightAdapter);
        mRightSpinner.setSelection(mSpinnerRightPosition,false);
 
        
        mRightSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v, int position,
                long id) {
          	  	String longCurrency = (String)mRightAdapter.getItem(position);
          	  mCurrencyRight = longCurrency.substring(0,longCurrency.indexOf(" "));
          	  
	          	mDb.updateDefaultValue("default_currency_right", mCurrencyRight);       	 
	      	  	
	      	  	Intent intent = new Intent(v.getContext(),Currencies.class);
	      	  	startActivity(intent);
	      	  	finish();
          	  
            }
  	        public void onNothingSelected(AdapterView arg0) {
  	          // NOP
  	        }
  	      });
        	
		mStillUpdating = true;
		
		
		
		Gallery g = (Gallery) findViewById(R.id.gallery_currencies);
        g.setAdapter(new ImageAdapterMarkets(mContext));
        
        g.setSelection(0);
        g.setSpacing(0);
        
        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
            	
            	((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(30);
            	
            	if(position == 1){
            		Intent intent = new Intent (v.getContext(),Commodities.class);           		
        			startActivity(intent);  
        			finish();
            	}
            	
            	if(position == 2){
            		Intent intent = new Intent (v.getContext(),Markets.class);
            		intent.putExtra("STILL_UPDATING", mStillUpdating);
            		intent.putExtra(Markets.IS_COMING_FROM_NAVIGATION, true);
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
        
        
        mGraphTimeframesRow = (TableRow)findViewById(R.id.currencies_graph_timeframe);
        
        TextView textView = (TextView)findViewById(R.id.currencies_timeframe_1d);      
        if(mDefaultTimeframe.compareTo("1d") == 0)  	  
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        
        textView = (TextView)findViewById(R.id.currencies_timeframe_5d);
        if(mDefaultTimeframe.compareTo("5d") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        textView = (TextView)findViewById(R.id.currencies_timeframe_3m);
        if(mDefaultTimeframe.compareTo("3m") == 0)
      	  textView.setTextColor(Color.YELLOW);   	 
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        textView = (TextView)findViewById(R.id.currencies_timeframe_1y);
        if(mDefaultTimeframe.compareTo("1y") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        textView = (TextView)findViewById(R.id.currencies_timeframe_2y);
        if(mDefaultTimeframe.compareTo("2y") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        
        textView = (TextView)findViewById(R.id.currencies_timeframe_5y);
        if(mDefaultTimeframe.compareTo("5y") == 0)
      	  textView.setTextColor(Color.YELLOW);   	  
        else 
      	  textView.setTextColor(Color.WHITE); 
        
        
        
		ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = serviceConn.getActiveNetworkInfo();
	    
        
	    
        
        if(mStillUpdating){
	        String url = "http://finance.yahoo.com/d/quotes.csv?s=" + mCurrencyLeft + mCurrencyRight  + "=X&f=sxl1c1p2t1d1hgkjj1ab2bb3pot8va2ren";
	        new DownloadSingleStockTask().execute(url);
        }
        
        
        
        mImage = (ImageButton)findViewById(com.pocketools.stockalert.R.id.graph_small);
     
        
        if(mNetworkInfo != null){
	        //new DownloadImageTask().execute("http://ichart.finance.yahoo.com/t?s=" + mSymbol);
	        new DownloadImageTask().execute("http://ichart.finance.yahoo.com/z?s=" + mCurrencyLeft + mCurrencyRight + "=X&t=" + mDefaultTimeframe + "&q=l&l=off&z=m&a=v&p=s");
        
        mCompanyNewsTable = (TableLayout)findViewById(com.pocketools.stockalert.R.id.company_news_table);
        String url = "http://www.google.com/finance/company_news?q=" + mCurrencyLeft + mCurrencyRight  + "&output=json&num=20";
        new DownloadNewsTask().execute(url);
             
        
        url = "http://gdata.youtube.com/feeds/api/videos?q=" +
        mCurrencyLeft + 
        "+company&orderby=relevance&start-index=1&max-results=10&v=2&alt=jsonc&category=News&time=this_week&safeSearch=strict&format=" + mVideoFormat;
       

        
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
       
        if(mCompanyNewsToggleExpanded){
        	mCompanyNewsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        	mCompanyNewsExpandable.setVisibility(View.VISIBLE);
        }
        else{
        	mCompanyNewsArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        	mCompanyNewsExpandable.setVisibility(View.GONE);
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
        
       
        
        
        try{
        	
        	/*
     // Set up GoogleAdView.
        GoogleAdView adView = (GoogleAdView) findViewById(R.id.adview_currency_page);
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
        	
        	TableLayout layout = (TableLayout) findViewById(R.id.top_text_view); 
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
    
        
        
        mTracker.dispatch();
        
      
       
   
    }
    
    public class ImageAdapterMarkets extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;

        private Integer[] mImageIds = {
        		R.drawable.currencies_on,
        		R.drawable.commodities_off,
        		R.drawable.markets_off,
                R.drawable.portfolio_off,
                R.drawable.alerts_off
        };

        
        public ImageAdapterMarkets(Context c) {
            mContext = c;
            
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


            return i;
        }
    }
    
	private class DownloadImageTask extends AsyncTask {
		
	     public Object doInBackground(Object... urls) {
	    	 
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
	    	 
	    	 return URLFetcher.getString((String)urls[0]); 
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
		    	 
		    	 
		    	 try{
			    	 while(tokenizer.hasMoreTokens()){
			    		 String token = tokenizer.nextToken();	    		 
			    		 
			    		 StringTokenizer subtokenizer = new StringTokenizer(token,",");
			    		 
			    		 
			    		// HashMap<String, String>stockMap = new HashMap<String, String>();
			    		 
			    		 symbol = subtokenizer.nextToken().replaceAll("\"", "");	
			    		// stockMap.put("symbol", symbol);
			    		 
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
			    		
			    		 mStillUpdating = false;
			    		 
			    	 }
		    	 }catch(NullPointerException e){}
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
			    
			 
			 
			 
			 if(jsonResult == null){ // If it's STILL null, then just go off the company name
				 String newURL = "http://www.google.com/finance/company_news?q=" + mCurrencyLeft + "&output=json&num=20"; 
				 jsonResult = (String)URLFetcher.getString(newURL);
			 }
			 
			    
	    	 
	    	 if(jsonResult != null){

    	 
		    	 String trimmedResult = Utilities.searchForString("{clusters:", ",results_per_page", new StringBuffer(jsonResult));
		    	 String decodedResult = trimmedResult.replace("x26#39;", "'");
		    	 decodedResult = decodedResult.replace("x26amp;", "&");
		    	 
	    	 
		    	 try{
		    		 
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
			    			 textTitle.setText("No Currency News Available");
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
    		
    		
    		
    		
    	}
    	
    	super.onWindowFocusChanged(hasFocus);
    }
	
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
	    	
	 	
	    	menu.add(0,MENU_REFRESH,0,"Refresh")
	 		.setIcon(android.R.drawable.ic_menu_rotate);
	    	
	    	//menu.add(0,MENU_CHANGE_INDEX,0,"Change Index")
	 		//.setIcon(android.R.drawable.ic_menu_more);
	    	
	    	menu.add(0,MENU_ADD_TO_PORTFOLIO,0,"Add to Portfolio")
	 		.setIcon(android.R.drawable.ic_menu_add);
	    	
	    	menu.add(0,MENU_INFO,0,"Info")
	 		.setIcon(android.R.drawable.ic_menu_help);
	    	
	    	menu.add(0,MENU_SETTINGS,0,"Settings")
	 		.setIcon(android.R.drawable.ic_menu_edit);

	    	
    	  return result;

    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        
        
        case SHOW_ADDED_TO_PORTFOLIO:
            
	        return new AlertDialog.Builder(this)
	        .setTitle(R.string.add_currency_to_portfolio_title)
	        .setMessage(R.string.add_to_portfolio_message)
	        .setIcon(android.R.drawable.ic_menu_add)
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
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {   
        
        
        
	    case MENU_REFRESH:
	    	
	    	Intent intent = new Intent (this,Currencies.class);
			startActivity(intent); 
			
			finish();
			return true; 
		
	    	case MENU_ADD_TO_PORTFOLIO:      

            DBAdapter db = new DBAdapter(this);
            db.open();

            db.addStock(mCurrencyLeft + mCurrencyRight + "=X", mCompanyName,mPrice);	                    
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
        
        
        if(!mDb.getDatabase().isOpen())
        	mDb.open();

        if(mCompanyNewsToggleExpanded)
        	mDb.updateDefaultValue("company_news_section_expanded", "on");
        else
        	mDb.updateDefaultValue("company_news_section_expanded", "off");
        
        
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
	        		intent.putExtra("SYMBOL", mCurrencyLeft + mCurrencyRight + "=X");

	      		
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
    
    
    
    
    private String[] currencyArray = { 
	"EUR  Euro      ",
	"GBP  United Kingdom Pounds",
	"USD  United States Dollars",
    "AFN  Afghanistan Afghanis",
    "AED  United Arab Emirates Dirhams",
    "ALL  Albania Leke",
    "ARS  Argentina Pesos",
    "AUD  Australia Dollars",
    "BBD  Barbados Dollars",
    "BDT  Bangladesh Taka",
    "BGN  Bulgaria Leva",
    "BHD  Bahrain Dinars",
    "BMD  Bermuda Dollars",
    "BRL  Brazil Reais",
    "BSD  Bahamas Dollars",
    "CAD  Canada Dollars",
    "CHF  Switzerland Francs",
    "CLP  Chile Pesos",
    "COP  Colombia Pesos",
    "CRC  Costa Rica Colones",
    "CNY  China Yuan Renminbi",
    "CZK  Czech Republic Koruny",
    "DKK  Denmark Kroner",
    "DOP  Dominican Republic Pesos",
    "DZD  Algeria Dinars",
    "EEK  Estonia Krooni",
    "EGP  Egypt Pounds",
    "EUR  Euro      ",
    "FJD  Fiji Dollars",
    "GBP  United Kingdom Pounds",
    "HKD  Hong Kong Dollars",
    "HRK  Croatia Kuna",
    "HUF  Hungary Forint",
    "IDR  Indonesia Rupiahs",
    "ILS  Israel New Shekels",
    "INR  India Rupees",
    "IQD  Iraq Dinars",
    "IRR  Iran Rials",
    "ISK  Iceland Kronur",
    "JMD  Jamaica Dollars",
    "JOD  Jordan Dinars",
    "KES  Kenya Shillings",
    "KRW  South Korea Won",
    "KWD  Kuwait Dinars",
    "LBP  Lebanon Pounds",
    "LKR  Sri Lanka Rupees",
    "MAD  Morocco Dirhams",
    "MUR  Mauritius Rupees",
    "MXN  Mexico Pesos",
    "MYR  Malaysia Ringgits",
    "NOK  Norway Kroner",
    "NZD  New Zealand Dollars",
    "OMR  Oman Rials",
    "PEN  Peru Nuevos Soles",
    "PHP  Philippines Pesos",
    "PKR  Pakistan Rupees",
    "PLN  Poland Zlotych",
    "QAR  Qatar Riyals",
    "RON  Romania New Lei",
    "RUB  Russia Rubles",
    "SAR  Saudi Arabia Riyals",
    "SDG  Sudan Pounds",
    "SEK  Sweden Kronor",
    "SGD  Singapore Dollars",
    "THB  Thailand Baht",
    "TND  Tunisia Dinars",
    "JPY  Japan Yen",
    "TRY  Turkey Lira",
    "TTD  Trinidad and Tobago Dollars",
    "TWD  Taiwan New Dollars",
    "USD  United States Dollars",
    "VEF  Venezuela Bolivares Fuertes",
    "VND  Vietnam Dong",
    "XAF  CFA BEAC Francs",
    "XAG  Silver Ounces",
    "XAU  Gold Ounces",
    "XCD  Eastern Caribbean Dollars",
    "XDR  IMF Special Drawing Right",
    "XOF  CFA BCEAO Francs",
    "XPD  Palladium Ounces",
    "XPF  CFP Francs",
    "XPT  Platinum Ounces",
    "ZAR  South Africa Rand",
    "ZMK  Zambia Kwacha",
    "NGN  Nigerian Naira"};
    
    
    
    
    
    
    
}