package com.pocketools.stockalert;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

/*
import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlTargeting;
import com.adwhirl.adapters.AdWhirlAdapter;
import com.google.ads.AdSenseSpec;
import com.google.ads.GoogleAdView;
import com.google.ads.AdSenseSpec.AdType;
import com.google.ads.AdSenseSpec.ExpandDirection;
*/

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;



public class Commodities extends Activity {
    /** Called when the activity is first created. */

	private DBAdapter mDb;
	
	
	private String mPrice;
	private static String YAHOO_ID = ".LRljOPV34HotkuMkLW2IRCXRn5UvSoUQscvtgmwA550PVIEAOQm7awmxCoifyiq6TYmUMx8aQbXOg--";

	private Context mContext;
	private boolean mStillUpdating = false;
	private TextView mStockDetailCompanyName;
	private TextView mStockDetailPrice;
	private TextView mStockDetailChange;
	private TextView mStockDailyHigh;
	private TextView mStockDailyLow;
	private TextView mStockDailyVolume;
	private TextView mStockLastTradeTime;
	private DownloadCommoditiesTask mCommoditiesTask;
	private NetworkInfo mNetworkInfo = null;
	private GoogleAnalyticsTracker mTracker;
	private HashMap<String, LinkedList<String>> mCommodityMap;
	private static final int MENU_REFRESH = 1001;
	private static final int MENU_INFO = 1002;
	private static final int MENU_SETTINGS = 1005;
	public static final int ALERT_DIALOG_USER_AGREEMENT = 1003;
	public static final int SHOW_ADDED_TO_PORTFOLIO = 1004;
	private TableLayout mEnergyTable;
	private TableLayout mMetalsTable;
	private TableLayout mGrainsTable;
	private TableLayout mLivestockTable;
	private TableLayout mSoftsTable;
	private TableLayout mIndicesTable;

	
	private String[] mCommodityTypes = {"Energy","Metals","Grains","Livestock","Softs","Indices"};
	
	
    //Increment this by 1 every time you want to show a splash screen to the user
    //The splash screen will only show once ever, unless we increment
    int mSplashScreenVersion = 4;
    
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
    private static final String CHANNEL_ID = "7470846041";
	
	

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mCommodityMap = new HashMap<String, LinkedList<String>>();
        
        mTracker = GoogleAnalyticsTracker.getInstance();        
        // Start the tracker in manual dispatch mode...
        mTracker.start(StockAlert.ANALYTICS_UA_NUMBER, this);
        mTracker.trackPageView("/Commodities");
        
        
        mContext = this;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        setContentView(com.pocketools.stockalert.R.layout.commodities); 
    
        
        mDb = new DBAdapter(this);
        mDb.open();
            

        Cursor defaults = mDb.getDefaultValues();
        startManagingCursor(defaults);
        
        for(int i=0; i< defaults.getCount(); i++){
        	defaults.moveToPosition(i);
        	
        	String default_type = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_TYPE)); 
        	    	
        	
        	if(default_type.compareTo("user_agreement_accepted") ==0){
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
        	
        	/*
        	else if(default_type.compareTo("google_keywords") ==0){
        		mKeywords = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	}
        	*/
        	
        }
        
 	
        
		
		Gallery g = (Gallery) findViewById(R.id.gallery_commodities);
        g.setAdapter(new ImageAdapterMarkets(mContext));
        
        g.setSelection(1);
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
        
        
        
        mEnergyTable = (TableLayout)findViewById(R.id.commodity_energy_list);
        mMetalsTable = (TableLayout)findViewById(R.id.commodity_metals_list);
        mGrainsTable = (TableLayout)findViewById(R.id.commodity_grains_list);
        mLivestockTable = (TableLayout)findViewById(R.id.commodity_livestock_list);
        mSoftsTable = (TableLayout)findViewById(R.id.commodity_softs_list);
        mIndicesTable = (TableLayout)findViewById(R.id.commodity_indices_list);
   
        
		ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mNetworkInfo = serviceConn.getActiveNetworkInfo();

        
        if(mNetworkInfo != null){
    
        	mCommoditiesTask = new DownloadCommoditiesTask();
        	mCommoditiesTask.execute("http://www.pocketsmart.net/stockalert/commodities1.csv");

        	
        	try{
        		
        	/*
		     // Set up GoogleAdView.
		        GoogleAdView adView = (GoogleAdView) findViewById(R.id.adview_commodities_page);
		        AdSenseSpec adSenseSpec =
		            new AdSenseSpec(CLIENT_ID)     // Specify client ID. (Required)
		            .setCompanyName(COMPANY_NAME)  // Set company name. (Required)
		            .setAppName(APP_NAME)          // Set application name. (Required)
		            .setKeywords(mKeywords)         // Specify keywords.
		            .setChannel(CHANNEL_ID)        // Set channel ID.
		            .setAdType(AdType.TEXT_IMAGE)        // Set ad type to Text.
		            .setExpandDirection(ExpandDirection.BOTTOM)
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
            	
            	TableLayout layout = (TableLayout) findViewById(R.id.top_text_view_commodities); 
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

       
   
    }
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        
        
        case SHOW_ADDED_TO_PORTFOLIO:
            
	        return new AlertDialog.Builder(this)
	        .setTitle(R.string.add_commodity_to_portfolio_title)
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
        		R.drawable.commodities_on,
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
	
	private class DownloadCommoditiesTask extends AsyncTask {
		
	     public Object doInBackground(Object... urls) {
	    	 	    	 
	    	 String commodityResult = URLFetcher.getString((String)urls[0]);
	    	 StringBuffer commodityList = new StringBuffer();

	    	 
	    	 StringTokenizer tokenizer = new StringTokenizer(commodityResult, "\n");
	    	 while(tokenizer.hasMoreTokens()){
	    		 
	    		 //CommodityUtil comm = new CommodityUtil();
	    		 
	    		 String token = tokenizer.nextToken();
	    		 
	    		 StringTokenizer subtokenizer = new StringTokenizer(token,",");
	    		 
	    		 String type = subtokenizer.nextToken();
	    		 String symbol = subtokenizer.nextToken();
	    		 
	    		 commodityList.append("+" + symbol);

	    		 if(mCommodityMap.containsKey(type)){
	    			 LinkedList<String> list = mCommodityMap.get(type);
	    			 list.add(symbol);
	    		 }
	    		 else{
	    			 LinkedList<String> list = new LinkedList<String>();
	    			 list.add(symbol);
	    			 mCommodityMap.put(type,list);
	    		 }
	    		 
	    	 }
	    	
	    	 return URLFetcher.getString("http://finance.yahoo.com/d/quotes.csv?s=" + commodityList.toString() + "&f=sl1c1p2t1d1n"); 
	     }

	     public void onPostExecute(Object result) {	   
	    	 
	    	 if(result != null){	
	    		 
		    	 String resultString = (String)result;		    	 
 
	    		 //http://finance.yahoo.com/d/quotes.csv?s=MSFT&f=sxl1c1p2t1d1hgkjj1ab2bb3pot8va2ren
	    		 //In order of csv
	    		 //Company name goes at the end due to some companies having a "," in the name
	    		 String symbol = ""; //s
	    		 //String stockExchange = ""; //x
	    		 String lastTrade = ""; //l1
	    		 String changeInPrice = ""; //c
	    		 String changeInPercentage = ""; //p2
	    		 String lastTradeTime = ""; //t1
	    		 String lastTradeDate = ""; //d1		    		 
	    		// String dailyHigh = ""; //h
	    		// String dailyLow = "";//g
	    		// String yearlyHigh = ""; //k
	    		// String yearlyLow = ""; //j
	    		// String fiftyTwoWeekRange = ""; //w
	    		// String marketCap = ""; //j1 		 
	    		// String askPrice = ""; //a
	    		// String askPriceRealtime = ""; //b2
	    		// String bidPrice = ""; //b
	    		// String bidPriceRealtime = ""; //b3
	    		// String previousClosePrice = ""; //p
	    		// String openPrice = ""; //o
	    		// String one_year_target_price = ""; //t8
	    		// String volume_average_3m = ""; //v
	    		// String volume_average_daily = ""; //a2
	    		// String priceEarningsRatio = ""; //r
	    		// String earningsPerShare = ""; //e
	    		 String companyName = ""; //n
		    	 
		    	 StringTokenizer tokenizer = new StringTokenizer(resultString, "\n");
		    	 
		    	 HashMap<String,CommodityUtil> commodityResults = new HashMap<String,CommodityUtil>();
		    	 

		    	 
		    	 
		    	 
		    	 try{
			    	 while(tokenizer.hasMoreTokens()){
			    		 
			    		 CommodityUtil commodity = new CommodityUtil();
			    		 
			    		 String token = tokenizer.nextToken();	    		 			    		 
			    		 StringTokenizer subtokenizer = new StringTokenizer(token,",");
			    		 	    		 
			    		 symbol = subtokenizer.nextToken().replaceAll("\"", "");	
			    		 commodity.setSymbol(symbol);
			    		// Log.d(getLocalClassName(),"Symbol = '" + symbol + "'");
			    		 
			    		 lastTrade = subtokenizer.nextToken().replaceAll("\"", "");	
			    		 commodity.setPrice(lastTrade);
			    		// Log.d(getLocalClassName(),"lastTrade = '" + lastTrade + "'");

			    		 changeInPrice = subtokenizer.nextToken().replaceAll("\"", "");
			    		 commodity.setChangeInPrice(changeInPrice);
			    		// Log.d(getLocalClassName(),"changeInPrice = '" + changeInPrice + "'");
			    		 
			    		 changeInPercentage = subtokenizer.nextToken().replaceAll("\"", "");
			    		 commodity.setChangeInPercentage(changeInPercentage);
			    		// Log.d(getLocalClassName(),"changeInPercentage = '" + changeInPercentage + "'");
			    		 
			    		 lastTradeTime = subtokenizer.nextToken().replaceAll("\"", "");				    		 
			    		 lastTradeDate = subtokenizer.nextToken().replaceAll("\"", "");				    		 
			    		 commodity.setLastTradeDate(lastTradeDate + " : " + lastTradeTime);
			    		// Log.d(getLocalClassName(),"lastTradeDate = '" + lastTradeDate + "'");
			    		 
			    		 companyName = subtokenizer.nextToken().replaceAll("\"", "");
			    		 companyName = companyName.trim();
			    		 commodity.setName(companyName);
			    		// Log.d(getLocalClassName(),"companyName = '" + companyName + "'");
			    		 
			    		 commodityResults.put(symbol, commodity);
			    		 
			    	 }
		    	 }catch(Exception e){}


		    	 
		    	 for(int i=0;i<mCommodityTypes.length;i++){
		    		 
		    		 String type = mCommodityTypes[i];
		    		 
			    	//Let's start with Energy
			    	 LinkedList<String> symbolList = mCommodityMap.get(type);
			    	 Iterator<String> iter = symbolList.iterator();
			    	 
			    	 while(iter.hasNext()){
			    		 symbol = iter.next();
			    		 CommodityUtil commodity = commodityResults.get(symbol);
	    		 
			    		 //First TableRow to be added to the overall table
			    		 TableRow row = new TableRow(mContext);

			    		 
			    		 
			    		 
			    		 TableLayout table2 = new TableLayout(mContext);
			    		 
			    		 TextView view = new TextView(mContext);
			    		 view.setText(commodity.getSymbol());
			    		 view.setTextColor(Color.WHITE);
			    		 view.setTextSize(18);
			    		 view.setGravity(Gravity.LEFT);
			    		 table2.addView(view);
			    		 			    		 
			    		 view = new TextView(mContext);
			    		 view.setText(commodity.getName());
			    		 view.setTextColor(Color.WHITE);
			    		 view.setTextSize(16);
			    		 view.setGravity(Gravity.LEFT);
			    		 table2.addView(view);
			    		 
			    		 
			    		 view = new TextView(mContext);
			    		 view.setText(commodity.getLastTradeDate());
			    		 view.setTextColor(Color.LTGRAY);
			    		 view.setTextSize(11);
			    		 table2.addView(view);
			    		 
			    		 row.addView(table2);
			    		 
			    		 TableLayout table3 = new TableLayout(mContext);
			    		 			    		 
			    		 view = new TextView(mContext);
			    		 view.setText(commodity.getPrice());
			    		 view.setTextColor(Color.WHITE);
			    		 view.setTextSize(18);
			    		 view.setGravity(Gravity.RIGHT);		    		 
			    		 table3.addView(view);
			    		 
			    		 
			    		 view = new TextView(mContext);
			    		 view.setText(commodity.getChangeInPrice() + " " + commodity.getChangeInPercentage() );
			    		 
			    		 if(commodity.getChangeInPrice().startsWith("-"))
			    			 view.setTextColor(Color.RED);
			    		 else
			    			 view.setTextColor(Color.GREEN);
			    		 
			    		 view.setTextSize(16);
			    		 view.setGravity(Gravity.RIGHT);
			    		 table3.addView(view);
			    		 
			    		 row.addView(table3);
			    		 
			    		 
			    		 TableLayout table1 = new TableLayout(mContext);


			    		 
			    		 ImageView image = new ImageView(mContext);
			    		 image.setImageDrawable(getResources().getDrawable(R.drawable.add));
			    		 image.setPadding(0, 15, 0, 0);
			    		 
			    		 image.setClickable(true);
			    		 
			    		 image.setTag(commodity);
			    		 
			    		 image.setOnClickListener(new View.OnClickListener(){
			    	        	public void onClick(View v){
			    	        		
			    	        		((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(30);
			    	        		
			    	        		DBAdapter db = new DBAdapter(getApplicationContext());
			                        db.open();
			                                    
			                        CommodityUtil util = (CommodityUtil)v.getTag();
			                        db.addStock(util.getSymbol(),util.getName(),util.getPrice());
			                        
			                        db.close();
			                        
			                        showDialog(SHOW_ADDED_TO_PORTFOLIO);

			    	        	}
			    	        });
			    		 	
			    		 table1.addView(image);
			    		 row.addView(table1);
			    		 

			    		 
			    		 if(type.compareTo("Energy")==0){
			    			 mEnergyTable.addView(row);	
			    			 
			    			 TextView breakLine = new TextView(mContext);
					    	 breakLine.setHeight(1);
					    	 breakLine.setPadding(4, 4, 4, 4);
					    	 breakLine.setBackgroundColor(Color.parseColor("#646D7E"));
					    	 mEnergyTable.addView(breakLine);
			    		 }
			    		 else if(type.compareTo("Metals")==0){
			    			 mMetalsTable.addView(row);
			    			 TextView breakLine = new TextView(mContext);
					    	 breakLine.setHeight(1);
					    	 breakLine.setPadding(2, 2, 2, 2);
					    	 breakLine.setBackgroundColor(Color.parseColor("#646D7E"));
					    	 mMetalsTable.addView(breakLine);
					    	 
			    		 }
			    		 else if(type.compareTo("Grains")==0){
			    			 mGrainsTable.addView(row);
			    			 TextView breakLine = new TextView(mContext);
					    	 breakLine.setHeight(1);
					    	 breakLine.setPadding(2, 2, 2, 2);
					    	 breakLine.setBackgroundColor(Color.parseColor("#646D7E"));
					    	 mGrainsTable.addView(breakLine);
			    		 }
			    		 else if(type.compareTo("Livestock")==0){
			    			 mLivestockTable.addView(row);
			    			 TextView breakLine = new TextView(mContext);
					    	 breakLine.setHeight(1);
					    	 breakLine.setPadding(2, 2, 2, 2);
					    	 breakLine.setBackgroundColor(Color.parseColor("#646D7E"));
					    	 mLivestockTable.addView(breakLine);
			    		 }
			    		 else if(type.compareTo("Softs")==0){
			    			 mSoftsTable.addView(row);
			    			 TextView breakLine = new TextView(mContext);
					    	 breakLine.setHeight(1);
					    	 breakLine.setPadding(2, 2, 2, 2);
					    	 breakLine.setBackgroundColor(Color.parseColor("#646D7E"));
					    	 mSoftsTable.addView(breakLine);
			    		 }
			    		 else if(type.compareTo("Indices")==0){
			    			 mIndicesTable.addView(row);
			    			 TextView breakLine = new TextView(mContext);
					    	 breakLine.setHeight(1);
					    	 breakLine.setPadding(2, 2, 2, 2);
					    	 breakLine.setBackgroundColor(Color.parseColor("#646D7E"));
					    	 mIndicesTable.addView(breakLine);
			    		 }
			    		 
			    		 
			    	 }
			    	 
			    	 
			    	 TextView energyLoading = (TextView)findViewById(R.id.commodity_energy_loading);
			    	 energyLoading.setVisibility(View.GONE);
			    	 
			    	 TextView metalsLoading = (TextView)findViewById(R.id.commodity_metals_loading);
			    	 metalsLoading.setVisibility(View.GONE);
			    	 
			    	 TextView grainsLoading = (TextView)findViewById(R.id.commodity_grains_loading);
			    	 grainsLoading.setVisibility(View.GONE);
			    	 
			    	 TextView livestockLoading = (TextView)findViewById(R.id.commodity_livestock_loading);
			    	 livestockLoading.setVisibility(View.GONE);
			    	 
			    	 TextView softsLoading = (TextView)findViewById(R.id.commodity_softs_loading);
			    	 softsLoading.setVisibility(View.GONE);
			    	 
			    	 TextView indicesLoading = (TextView)findViewById(R.id.commodity_indices_loading);
			    	 indicesLoading.setVisibility(View.GONE);
			    	 
		    	 }
		    	 
		    	 
	    	 } 
	    	 
	    	 
	    	 
	    	 
   	 
	     }
	}
	
	
	

	
	
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
	    	
	 	
	    	menu.add(0,MENU_REFRESH,0,"Refresh")
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
        
        
	    case MENU_REFRESH:
	    	
	    	Intent intent = new Intent (this,Commodities.class);
			startActivity(intent); 
			
			finish();
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
        
        
        if(mDb.getDatabase().isOpen())
        	mDb.close();
        
        
        if(mCommoditiesTask != null && mCommoditiesTask.getStatus() == AsyncTask.Status.RUNNING)
        	mCommoditiesTask.cancel(true);
  
        super.onStop();
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