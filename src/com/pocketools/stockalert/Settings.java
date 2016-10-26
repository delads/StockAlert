package com.pocketools.stockalert;


import java.util.HashMap;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.AdapterView.OnItemSelectedListener;



public class Settings extends Activity {
    /** Called when the activity is first created. */
	
	DBAdapter mDb;
	String mDefaultPage;
	String mDefaultTimeframe;
	String mDefaultLocation;
	ArrayAdapter<String> mArrayAdapter;
	ArrayAdapter<String> mTimeframeAdapter;
	ArrayAdapter<String> mLocationAdapter;
	GoogleAnalyticsTracker mTracker;
	int mGraphPosition = 0;
	int mDefaultPagePosition = 0;
	int mDefaultLocationPosition = 0;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mTracker = GoogleAnalyticsTracker.getInstance();       
        // Start the tracker in manual dispatch mode...
        mTracker.start(StockAlert.ANALYTICS_UA_NUMBER, this);
        mTracker.trackPageView("/Settings");
        
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);       
        setContentView(R.layout.settings);

        mDb = new DBAdapter(this);
        mDb.open();
        
        Cursor defaults = mDb.getDefaultValues();
        startManagingCursor(defaults);
        
        for(int i=0; i< defaults.getCount(); i++){
        	defaults.moveToPosition(i);
        	
        	String default_type = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_TYPE)); 
        	
        	if(default_type.compareTo("default_page") ==0)
        		mDefaultPage = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	
        	else if(default_type.compareTo("default_timeframe") ==0){
        		mDefaultTimeframe = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	}  
        	
        	else if(default_type.compareTo("default_location") ==0){
        		mDefaultLocation = defaults.getString(defaults.getColumnIndex(DBAdapter.KEY_DEFAULT_VALUE));
        	}
        }
        
        
        
        for(int i=0; i< pageArray.length; i++){	      	
        	String defaultPage = pageArray[i];
      	
        	if(defaultPage.compareTo(mDefaultPage) == 0)
        		mDefaultPagePosition = i;
        }    
        
        
        
        for(int j=0; j< timeframeArray.length; j++){	      	
        	String timeframe = timeframeArray[j];
        	
        	String expandedString = consts.get(timeframe);
        	if(expandedString != null && expandedString.compareTo(mDefaultTimeframe) == 0)
        		mGraphPosition = j;
        	
        }  
        
        for(int k=0; k< locationArray.length; k++){	      	
        	String location = locationArray[k];
        	
        	String expandedString = consts2.get(location);
        	if(expandedString != null && expandedString.compareTo(mDefaultLocation) == 0)
        		mDefaultLocationPosition = k;
        	
        }  
 
 

            
        
        Spinner spinner = (Spinner) findViewById(R.id.default_page_spinner);
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pageArray);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mArrayAdapter);
        spinner.setSelection(mDefaultPagePosition);
      
        
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
    		public void onItemSelected(AdapterView parent, View v, int position,
                  long id) {
    				mDefaultPage = (String)mArrayAdapter.getItem(position);			
              }
    	        public void onNothingSelected(AdapterView arg0) {
    	          // NOP
    	        }
    	      });

        
        Button saveButton = (Button)findViewById(R.id.set_default_page_button);      
        saveButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	
            	mDb.updateDefaultPage(mDefaultPage);         	
            	mDb.close();
            	
    	    	Intent intent = new Intent(v.getContext(),Markets.class);
    	    	startActivity(intent);
    	    	finish();
    	    	
            }
        });
        
        /*
        TableRow row = (TableRow)findViewById(R.id.settings_widget_row);
        row.setClickable(true);
        
        row.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v){
        		
        		((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(30);
        		Intent intent = new Intent (v.getContext(),WidgetConfiguration.class);
    			startActivity(intent);      		

        	}
        });
        */
        
        
        
        Spinner timeframeSpinner = (Spinner) findViewById(R.id.default_timeframe_spinner);
        mTimeframeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeframeArray);
        mTimeframeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeframeSpinner.setAdapter(mTimeframeAdapter);
        timeframeSpinner.setSelection(mGraphPosition);
      
        
        timeframeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
    		public void onItemSelected(AdapterView parent, View v, int position,
                  long id) {
    				String timeframe = (String)mTimeframeAdapter.getItem(position);	
    				mDefaultTimeframe = consts.get(timeframe);
              }
    	        public void onNothingSelected(AdapterView arg0) {
    	          // NOP
    	        }
    	      });

        
        Button saveTimeframeButton = (Button)findViewById(R.id.set_default_timeframe_button);      
        saveTimeframeButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	
            	
            	mDb.updateDefaultTimeframe(mDefaultTimeframe);         	
            	mDb.close();
            	
    	    	Intent intent = new Intent(v.getContext(),Markets.class);
    	    	startActivity(intent);
    	    	finish();
    	    	
            }
        });
        
        
        Spinner locationSpinner = (Spinner) findViewById(R.id.default_location_spinner);
        mLocationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationArray);
        mLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(mLocationAdapter);
        locationSpinner.setSelection(mDefaultLocationPosition);
      
        
        locationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
    		public void onItemSelected(AdapterView parent, View v, int position,
                  long id) {
    			String location = (String)mLocationAdapter.getItem(position);
    			mDefaultLocation = consts2.get(location);
              }
    	        public void onNothingSelected(AdapterView arg0) {
    	          // NOP
    	        }
    	      });

        
        Button saveLocationButton = (Button)findViewById(R.id.set_default_location_button);      
        saveLocationButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	
            	
            	mDb.updateDefaultLocation(mDefaultLocation);         	
            	mDb.close();
            	
    	    	Intent intent = new Intent(v.getContext(),Markets.class);
    	    	startActivity(intent);
    	    	finish();
    	    	
            }
        });
        
        Button currentLocationButton = (Button)findViewById(R.id.set_default_location_button_current);      
        currentLocationButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	
            	
            	mDb.updateDefaultLocation("Current Location");         	
            	mDb.close();
            	
    	    	Intent intent = new Intent(v.getContext(),Markets.class);
    	    	startActivity(intent);
    	    	finish();
    	    	
            }
        });
        
        
        
        mTracker.dispatch();
       
    }
    
    
    
    public static final String MARKETS_PAGE = "Markets Page";
    public static final String PORTFOLIO_PAGE = "Portfolio Page";
    public static final String ALERTS_PAGE = "Alerts Page";
    public static final String COMMODITIES_PAGE = "Commodities Page";
    public static final String CURRENCY_PAGE = "Currency Page";
    public static final String IS_COMING_FROM_NAVIGATION = "Navigation";
    
    
    public static final String ONE_DAY = "1 Day";
    public static final String FIVE_DAY = "5 Day";
    public static final String THREE_MONTH = "3 Month";
    public static final String ONE_YEAR = "1 Year";
    public static final String TWO_YEAR = "2 Year";
    public static final String FIVE_YEAR = "5 Year";
    
    private String[] locationArray = {
    		"Abbotsford",
    		"Abilene, TX",
    		"Akron / Canton",
    		"Albany / Capital Region",
    		"Albuquerque",
    		"Allentown / Reading",
    		"Amarillo",
    		"Anchorage",
    		"Ann Arbor",
    		"Asheville",
    		"Athens, GA",
    		"Atlanta",
    		"Augusta",
    		"Austin",
    		"Bakersfield",
    		"Baltimore",
    		"Barrie",
    		"Baton Rouge",
    		"Beaumont, TX",
    		"Billings / Bozeman",
    		"Birmingham",
    		"Bloomington, IN",
    		"Boise",
    		"Boston",
    		"Buffalo",
    		"Calgary",
    		"Cape Breton",
    		"Cedar Rapids / Iowa City",
    		"Central Jersey",
    		"Charleston",
    		"Charlotte",
    		"Chattanooga",
    		"Chicago",
    		"Cincinnati",
    		"Cleveland",
    		"Colorado Springs",
    		"Columbia",
    		"Columbia, MO",
    		"Columbus",
    		"Columbus GA",
    		"Corpus Christi",
    		"Dallas",
    		"Dayton",
    		"Daytona Beach",
    		"Denver",
    		"Des Moines",
    		"Detroit",
    		"Edmonton",
    		"El Paso",
    		"Erie",
    		"Eugene",
    		"Evansville",
    		"Fairfield County",
    		"Fort Lauderdale",
    		"Fort Myers / Cape Coral",
    		"Fort Wayne",
    		"Fort Worth",
    		"Fox Cities",
    		"Fresno",
    		"Gainesville",
    		"Grand Rapids",
    		"Green Bay",
    		"Greenville",
    		"Halifax",
    		"Hampton Roads",
    		"Harrisburg / Lancaster",
    		"Hartford",
    		"Honolulu",
    		"Houston",
    		"Huntsville",
    		"Indianapolis",
    		"Inland Empire",
    		"Jackson",
    		"Jacksonville",
    		"Kalamazoo",
    		"Kansas City",
    		"Kelowna",
    		"Kingston / Belleville",
    		"Kitchener - Waterloo",
    		"Knoxville",
    		"Lakeland",
    		"Lansing",
    		"Las Vegas",
    		"Lexington",
    		"Lincoln",
    		"Little Rock",
    		"London, ON",
    		"Long Island",
    		"Los Angeles",
    		"Louisville",
    		"Lubbock",
    		"Macon",
    		"Madison",
    		"Memphis",
    		"Miami",
    		"Midland / Odessa",
    		"Milwaukee",
    		"Minneapolis / St Paul",
    		"Mobile / Baldwin County",
    		"Modesto",
    		"Montgomery",
    		"Napa / Sonoma",
    		"Naples",
    		"Nashville",
    		"New Orleans",
    		"New York City",
    		"North Jersey",
    		"Ocala",
    		"Ogden",
    		"Oklahoma City",
    		"Omaha",
    		"Orange County",
    		"Orlando",
    		"Ottawa",
    		"Palm Beach",
    		"Pensacola / Emerald Coast",
    		"Philadelphia",
    		"Phoenix",
    		"Piedmont Triad",
    		"Pittsburgh",
    		"Portland",
    		"Portland, ME",
    		"Providence",
    		"Raleigh / Durham",
    		"Regina",
    		"Reno",
    		"Richmond",
    		"Rio Grande Valley",
    		"Roanoke",
    		"Rochester",
    		"Rockford",
    		"Sacramento",
    		"Salem OR",
    		"Salt Lake City",
    		"San Angelo",
    		"San Antonio",
    		"San Diego",
    		"San Francisco",
    		"San Jose",
    		"Santa Barbara",
    		"Santa Clarita",
    		"Santa Cruz / Monterey",
    		"Saskatoon",
    		"Savannah / Hilton Head",
    		"Seattle",
    		"Shreveport / Bossier",
    		"Sioux Falls",
    		"South Bend",
    		"Southern Georgia",
    		"Spokane / Coeur d'Alene",
    		"Springfield MO",
    		"Springfield, MA",
    		"St Catharines-Niagara",
    		"St John's",
    		"St Louis",
    		"Stockton",
    		"Sudbury / North Bay",
    		"Syracuse",
    		"Tallahassee",
    		"Tampa Bay Area",
    		"Toledo",
    		"Topeka / Lawrence",
    		"Toronto (GTA)",
    		"Tucson",
    		"Tulsa",
    		"Vancouver",
    		"Ventura County",
    		"Victoria",
    		"Washington DC",
    		"Westchester County",
    		"Wichita",
    		"Wilmington-Newark",
    		"Windsor",
    		"Winnipeg",
    		"Worcester",
    		"Youngstown"
    };
    
    private String[] pageArray = { 
    		Markets.MARKETS_PAGE,
    		Markets.PORTFOLIO_PAGE,
    		Markets.ALERTS_PAGE,
    		Markets.COMMODITIES_PAGE,
    		Markets.CURRENCY_PAGE};
    
    private String[] timeframeArray = { 
    		ONE_DAY,
    		FIVE_DAY,
    		THREE_MONTH,
    		ONE_YEAR,
    		TWO_YEAR,
    		FIVE_YEAR};
    
    public final static HashMap<String, String> consts = new HashMap<String, String>();
	static
	{
		consts.put(ONE_DAY,"1d");
    	consts.put(FIVE_DAY,"5d");
    	consts.put(THREE_MONTH,"3m");
    	consts.put(ONE_YEAR,"1y");
    	consts.put(TWO_YEAR,"2y");
    	consts.put(FIVE_YEAR,"5y");
	}
	
	public final static HashMap<String, String> consts2 = new HashMap<String, String>();
	static
	{
		consts2.put("Abbotsford","abbotsford");
		 consts2.put("Abilene, TX","abilene");
		  consts2.put("Akron / Canton","akron-canton");
		  consts2.put("Albany / Capital Region","albany-capital-region");
		 consts2.put("Albuquerque","albuquerque");
		 consts2.put("Allentown / Reading","allentown-reading");
		 consts2.put("Amarillo","amarillo");
		 consts2.put("Anchorage","anchorage");
		consts2.put("Ann Arbor","ann-arbor");
		 consts2.put("Asheville","asheville");
		 consts2.put("Athens, GA","athens-ga");
		consts2.put("Atlanta","atlanta");
		 consts2.put("Augusta","augusta");
		 consts2.put("Austin","austin");
		 consts2.put("Bakersfield","bakersfield");
		 consts2.put("Baltimore","baltimore");
		 consts2.put("Barrie","barrie");
		 consts2.put("Baton Rouge","baton-rouge");
		 consts2.put("Beaumont, TX","beaumont");
		 consts2.put("Billings / Bozeman","billings");
		 consts2.put("Birmingham","birmingham");
		 consts2.put("Bloomington, IN","bloomington-in");
		 consts2.put("Boise","boise");
		 consts2.put("Boston","boston");
		 consts2.put("Buffalo","buffalo");
		 consts2.put("Calgary","calgary");
		 consts2.put("Cape Breton","cape-breton");
		 consts2.put("Cedar Rapids / Iowa City","cedar-rapids-iowa-city");
		 consts2.put("Central Jersey","central-jersey");
		 consts2.put("Charleston","charleston");
		 consts2.put("Charlotte","charlotte");
		 consts2.put("Chattanooga","chattanooga");
		 consts2.put("Chicago","chicago");
		 consts2.put("Cincinnati","cincinnati");
		 consts2.put("Cleveland","cleveland");
		 consts2.put("Colorado Springs","colorado-springs");
		 consts2.put("Columbia","columbia");
		 consts2.put("Columbia, MO","columbia-mo");
		 consts2.put("Columbus","columbus");
		 consts2.put("Columbus GA","columbus-ga");
		 consts2.put("Corpus Christi","corpus-christi");
		 consts2.put("Dallas","dallas");
		 consts2.put("Dayton","dayton");
		 consts2.put("Daytona Beach","daytona-beach");
		 consts2.put("Denver","denver");
		 consts2.put("Des Moines","des-moines");
		 consts2.put("Detroit","detroit");
		 consts2.put("Edmonton","edmonton");
		 consts2.put("El Paso","el-paso");
		 consts2.put("Erie","erie");
		 consts2.put("Eugene","eugene");
		 consts2.put("Evansville","evansville");
		 consts2.put("Fairfield County","fairfield-county");
		 consts2.put("Fort Lauderdale","fort-lauderdale");
		 consts2.put("Fort Myers / Cape Coral","fort-myers-cape-coral");
		 consts2.put("Fort Wayne","fort-wayne");
		 consts2.put("Fort Worth","fort-worth");
		 consts2.put("Fox Cities","appleton");
		 consts2.put("Fresno","fresno");
		 consts2.put("Gainesville","gainesville");
		 consts2.put("Grand Rapids","grand-rapids");
		 consts2.put("Green Bay","green-bay");
		 consts2.put("Greenville","greenville");
		 consts2.put("Halifax","halifax");
		 consts2.put("Hampton Roads","hampton-roads");
		 consts2.put("Harrisburg / Lancaster","harrisburg");
		 consts2.put("Hartford","hartford");
		 consts2.put("Honolulu","honolulu");
		 consts2.put("Houston","houston");
		 consts2.put("Huntsville","huntsville");
		 consts2.put("Indianapolis","indianapolis");
		 consts2.put("Inland Empire","inland-empire");
		 consts2.put("Jackson","jackson");
		 consts2.put("Jacksonville","jacksonville");
		 consts2.put("Kalamazoo","kalamazoo");
		 consts2.put("Kansas City","kansas-city");
		 consts2.put("Kelowna","kelowna");
		 consts2.put("Kingston / Belleville","kingston");
		 consts2.put("Kitchener - Waterloo","kitchener-waterloo");
		 consts2.put("Knoxville","knoxville");
		 consts2.put("Lakeland","lakeland");
		 consts2.put("Lansing","lansing");
		 consts2.put("Las Vegas","las-vegas");
		 consts2.put("Lexington","lexington");
		 consts2.put("Lincoln","lincoln");
		 consts2.put("Little Rock","little-rock");
		 consts2.put("London, ON","london");
		 consts2.put("Long Island","long-island");
		 consts2.put("Los Angeles","los-angeles");
		 consts2.put("Louisville","louisville");
		 consts2.put("Lubbock","lubbock");
		 consts2.put("Macon","macon");
		 consts2.put("Madison","madison");
		 consts2.put("Memphis","memphis");
		 consts2.put("Miami","miami");
		 consts2.put("Midland / Odessa","midland-odessa");
		 consts2.put("Milwaukee","milwaukee");
		 consts2.put("Minneapolis / St Paul","minneapolis-stpaul");
		 consts2.put("Mobile / Baldwin County","mobile-baldwin-county");
		 consts2.put("Modesto","modesto");
		 consts2.put("Montgomery","montgomery");
		 consts2.put("Napa / Sonoma","napa-sonoma");
		 consts2.put("Naples","naples");
		 consts2.put("Nashville","nashville");
		 consts2.put("New Orleans","new-orleans");
		 consts2.put("New York City","new-york");
		 consts2.put("North Jersey","north-jersey");
		 consts2.put("Ocala","ocala");
		 consts2.put("Ogden","ogden");
		 consts2.put("Oklahoma City","oklahoma-city");
		 consts2.put("Omaha","omaha");
		 consts2.put("Orange County","orange-county");
		 consts2.put("Orlando","orlando");
		 consts2.put("Ottawa","ottawa");
		 consts2.put("Palm Beach","palm-beach");
		 consts2.put("Pensacola / Emerald Coast","pensacola");
		 consts2.put("Philadelphia","philadelphia");
		 consts2.put("Phoenix","phoenix");
		 consts2.put("Piedmont Triad","piedmont-triad");
		 consts2.put("Pittsburgh","pittsburgh");
		 consts2.put("Portland","portland");
		 consts2.put("Portland, ME","portland-me");
		 consts2.put("Providence","providence");
		 consts2.put("Raleigh / Durham","raleigh-durham");
		 consts2.put("Regina","regina");
		 consts2.put("Reno","reno");
		 consts2.put("Richmond","richmond");
		 consts2.put("Rio Grande Valley","rio-grande-valley");
		 consts2.put("Roanoke","roanoke");
		 consts2.put("Rochester","rochester");
		 consts2.put("Rockford","rockford");
		 consts2.put("Sacramento","sacramento");
		 consts2.put("Salem OR","salem-or");
		 consts2.put("Salt Lake City","salt-lake-city");
		 consts2.put("San Angelo","san-angelo");
		 consts2.put("San Antonio","san-antonio");
		 consts2.put("San Diego","san-diego");
		 consts2.put("San Francisco","san-francisco");
		 consts2.put("San Jose","san-jose");
		 consts2.put("Santa Barbara","santa-barbara");
		 consts2.put("Santa Clarita","santa-clarita");
		 consts2.put("Santa Cruz / Monterey","santa-cruz");
		 consts2.put("Saskatoon","saskatoon");
		 consts2.put("Savannah / Hilton Head","savannah-hilton-head");
		 consts2.put("Seattle","seattle");
		 consts2.put("Shreveport / Bossier","shreveport-bossier");
		 consts2.put("Sioux Falls","sioux-falls");
		 consts2.put("South Bend","south-bend");
		 consts2.put("Southern Georgia","albany-ga");
		 consts2.put("Spokane / Coeur d'Alene","spokane-coeur-dalene");
		 consts2.put("Springfield MO","springfield-mo");
		 consts2.put("Springfield, MA","springfield-ma");
		 consts2.put("St Catharines-Niagara","stcatharines-niagara");
		 consts2.put("St John's","st-johns");
		 consts2.put("St Louis","stlouis");
		 consts2.put("Stockton","stockton");
		 consts2.put("Sudbury / North Bay","sudbury");
		 consts2.put("Syracuse","syracuse");
		 consts2.put("Tallahassee","tallahassee");
		 consts2.put("Tampa Bay Area","tampa-bay-area");
		 consts2.put("Toledo","toledo");
		 consts2.put("Topeka / Lawrence","topeka-lawrence");
		 consts2.put("Toronto (GTA);","greater-toronto-area");
		 consts2.put("Tucson","tucson");
		 consts2.put("Tulsa","tulsa");
		 consts2.put("Vancouver","vancouver");
		 consts2.put("Ventura County","ventura-county");
		 consts2.put("Victoria","victoria");
		 consts2.put("Washington DC","washington-dc");
		 consts2.put("Westchester County","westchester-county");
		 consts2.put("Wichita","wichita");
		 consts2.put("Wilmington-Newark","wilmington-newark");
		 consts2.put("Windsor","windsor");
		 consts2.put("Winnipeg","winnipeg");
		 consts2.put("Worcester","worcester");
		 consts2.put("Youngstown","youngstown");
	}
	
	
   
    @Override
    protected void onSaveInstanceState(Bundle outState) {      
        //Log.d(this.getLocalClassName(), "onSaveInstanceState()");
        
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(this,Markets.class);
            startActivity(intent);
            finish();
            
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onPause() {
        //Log.d(this.getLocalClassName(), "onPause()");
        
        if(mDb.getDatabase().isOpen())
        	mDb.close();

        super.onPause();
    }
    
    @Override
    protected void onStop() {      
        //Log.d(this.getLocalClassName(), "onStop()");
        

        
        if(mDb.getDatabase().isOpen())
        	mDb.close();
  
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
        //Log.d(this.getLocalClassName(), "onDestroy()");
        
        if(mDb.getDatabase().isOpen())
        	mDb.close();
        
        super.onDestroy();
        
        mTracker.stop();
    }
    
    @Override
    protected void onResume() {
    	//Log.d(this.getLocalClassName(), "onResume()");
    	
    	if(!mDb.getDatabase().isOpen())
    		mDb.open();
    	
    	super.onResume();
    }
    
    @Override
    protected void onRestart() {        
    	//Log.d(this.getLocalClassName(), "onRestart()");
    	
    	if(!mDb.getDatabase().isOpen())
    		mDb.open();
      
        super.onRestart();
    }
    
    
    protected void onStart() {
    	//Log.d(this.getLocalClassName(), "onStart()");
    	
    	if(!mDb.getDatabase().isOpen())
    		mDb.open();
	
        super.onStart();  
}

 
}