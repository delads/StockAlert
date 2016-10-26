package com.pocketools.stockalert;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.pocketools.stockalert.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class Graph extends Activity {
    /** Called when the activity is first created. */

    private ImageButton mImageButton;
    private View mGraphView = null;
    private TableRow mTimeframeRow;
    private String mTickerLeft;
    private String mTickerRight;
    private String mTimeframe;
    private Spinner mSpinnerLeft;
    private ArrayAdapter mLeftAdapter;
    private ArrayAdapter mRightAdapter;
    private Spinner mSpinnerRight;
    private int mSpinnerLeftPosition = 0;
    private int mSpinnerRightPosition = 0;
    private String mUrl;
    boolean firstCallLeft = true;
    boolean firstCallRight = true;
    GoogleAnalyticsTracker mTracker;
    DBAdapter mDb;
    ArrayList<String> currencyArray;
    //AdView mAdView;
   // List<Bitmap> mBitmapList;
   // Gallery mGallery;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        mTracker = GoogleAnalyticsTracker.getInstance();       
        // Start the tracker in manual dispatch mode...
        mTracker.start(StockAlert.ANALYTICS_UA_NUMBER, this);
        mTracker.trackPageView("/Graph");
        
        
        
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        
        if(mGraphView == null){
        	mGraphView = LayoutInflater.from(this).inflate(R.layout.graph, null);
        }
        
        setContentView(mGraphView);   
        
        
        
      //  mBitmapList = new LinkedList<Bitmap>();
        currencyArray = new ArrayList<String>();
     
        
        mDb = new DBAdapter(this);
        mDb.open();
        
        Cursor currencies = mDb.getStocks();
        startManagingCursor(currencies);
        
        for(int i=0; i < currencies.getCount(); i++){
        	
        	currencies.moveToPosition(i);
        	      	
        	currencyArray.add(currencies.getString(currencies.getColumnIndex(DBAdapter.KEY_SYMBOL)) + "   -  " + 
        			currencies.getString(currencies.getColumnIndex(DBAdapter.KEY_COMPANY_NAME)));
        }
        
       
     //   mAdView = (AdView) findViewById(R.id.ad_currency_graph);
               
        mTimeframe = this.getIntent().getExtras().getString("TIMEFRAME");
        mTickerLeft = this.getIntent().getExtras().getString("CURRENCY_LEFT");
        mTickerRight = this.getIntent().getExtras().getString("CURRENCY_RIGHT");
        
       
        StringBuffer urlBuffer = new StringBuffer(consts.get(mTimeframe).replace("TICKER", mTickerLeft));
        
        if(mTickerLeft.compareTo(mTickerRight) != 0)
        	urlBuffer.append("&c=" + mTickerRight);
        
        String url = urlBuffer.toString();
        
        
       mImageButton = (ImageButton) findViewById(R.id.graphButton); 
       

       
       
       Iterator<String> iter = currencyArray.iterator();
	    
	    int count = 0;
	    
        while(iter.hasNext()){
	      	
	       	String longCurrency = iter.next();
	       	
	       	String currency = longCurrency.substring(0,longCurrency.indexOf(" "));     	
	       	
	       	if(currency.compareTo(mTickerLeft) == 0)
	       		mSpinnerLeftPosition = count;
	       	
	       	if(currency.compareTo(mTickerRight) == 0)
	       		mSpinnerRightPosition = count;
       	
	       	count++;
       }      
       
       
       mSpinnerLeft = (Spinner)findViewById(R.id.graph_spinner_left);
       mLeftAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyArray);
       mLeftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       mSpinnerLeft.setAdapter(mLeftAdapter);
       mSpinnerLeft.setSelection(mSpinnerLeftPosition);
       
       
       mSpinnerRight = (Spinner)findViewById(R.id.graph_spinner_right);
       mRightAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyArray);
       mRightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       mSpinnerRight.setAdapter(mRightAdapter);
       mSpinnerRight.setSelection(mSpinnerRightPosition);
       
       
       // This listener is used to set the selected timeframe from the Spinner.
       mSpinnerLeft.setOnItemSelectedListener(new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View v, int position,
             long id) {
				String longCurrency = (String)mLeftAdapter.getItem(position);
				mTickerLeft = longCurrency.substring(0,longCurrency.indexOf(" "));
				
				if(!firstCallLeft){
					
					//mAdView.setKeywords(PocketCurrency.AD_MOB_KEYWORD_HINT + mCurrencyLeft + " " + mCurrencyRight);
					//mAdView.requestFreshAd(); 
					 
					try{					
						if(mTickerLeft.compareTo(mTickerRight) == 0)
							mUrl = consts.get(mTimeframe).replace("TICKER", mTickerLeft);
						
						else
							mUrl = consts.get(mTimeframe).replace("TICKER", mTickerLeft) + "&c=" + mTickerRight;
						
			        	new DownloadImageTask(v.getContext()).execute(mUrl);
		       	
			        }catch (Exception e){Log.e("PocketCurrency", "Application Exited Abnormally !");}
				}
				else
					firstCallLeft = false;
         	  	
		}
        public void onNothingSelected(AdapterView arg0) {
          // NOP
        }
      });
       
       
    // This listener is used to set the selected timeframe from the Spinner.
       mSpinnerRight.setOnItemSelectedListener(new OnItemSelectedListener() {
		public void onItemSelected(AdapterView parent, View v, int position,
             long id) {
				String longCurrency = (String)mRightAdapter.getItem(position);
				mTickerRight = longCurrency.substring(0,longCurrency.indexOf(" "));
				
				if(!firstCallRight){
					
					//mAdView.setKeywords(PocketCurrency.AD_MOB_KEYWORD_HINT + mCurrencyLeft + " " + mCurrencyRight);
					//mAdView.requestFreshAd(); 
					 
					try{
						if(mTickerLeft.compareTo(mTickerRight) == 0)
							mUrl = consts.get(mTimeframe).replace("TICKER", mTickerLeft);
						
						else
							mUrl = consts.get(mTimeframe).replace("TICKER", mTickerLeft) + "&c=" + mTickerRight;
						
			        	new DownloadImageTask(v.getContext()).execute(mUrl);
		       	
			        }catch (Exception e){Log.e("PocketCurrency", "Application Exited Abnormally !");}
				}
				else
					firstCallRight = false;
         	  	
		}
        public void onNothingSelected(AdapterView arg0) {
          // NOP
        }
      });
	

       
       
       
       
       
       mImageButton.setOnClickListener(new Button.OnClickListener() {
           public void onClick(View v) {
          	
           	Intent intent = new Intent(v.getContext(),GraphDuration.class); 
           	startActivityForResult(intent, GraphDuration.CHOOSE_TIMEFRAME);
           }
       });
       
       
       

       mTimeframeRow = (TableRow)findViewById(R.id.graph_timeframeRow);
       mTimeframeRow.setClickable(true);
       
       
       mTimeframeRow.setOnClickListener(new Button.OnClickListener() {
           public void onClick(View v) {
          	
           	Intent intent = new Intent(v.getContext(),GraphDuration.class);         
           	startActivityForResult(intent, GraphDuration.CHOOSE_TIMEFRAME);
           }
       });        
   
   
      TextView textView = (TextView)findViewById(R.id.graph_timeframe_1d);      
      if(mTimeframe.compareTo("1 day") == 0)  	  
    	  textView.setTextColor(Color.YELLOW);   	  
      else 
    	  textView.setTextColor(Color.WHITE); 
      
      
      textView = (TextView)findViewById(R.id.graph_timeframe_5d);
      if(mTimeframe.compareTo("5 day") == 0)
    	  textView.setTextColor(Color.YELLOW);   	  
      else 
    	  textView.setTextColor(Color.WHITE); 
      
      textView = (TextView)findViewById(R.id.graph_timeframe_3m);
      if(mTimeframe.compareTo("3 month") == 0)
    	  textView.setTextColor(Color.YELLOW);   	 
      else 
    	  textView.setTextColor(Color.WHITE); 
      
      textView = (TextView)findViewById(R.id.graph_timeframe_1y);
      if(mTimeframe.compareTo("1 year") == 0)
    	  textView.setTextColor(Color.YELLOW);   	  
      else 
    	  textView.setTextColor(Color.WHITE); 
      
      textView = (TextView)findViewById(R.id.graph_timeframe_2y);
      if(mTimeframe.compareTo("2 year") == 0)
    	  textView.setTextColor(Color.YELLOW);   	  
      else 
    	  textView.setTextColor(Color.WHITE); 
      
      
      textView = (TextView)findViewById(R.id.graph_timeframe_5y);
      if(mTimeframe.compareTo("5 year") == 0)
    	  textView.setTextColor(Color.YELLOW);   	  
      else 
    	  textView.setTextColor(Color.WHITE); 
       
      
       ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = serviceConn.getActiveNetworkInfo();
	    
	    if(networkInfo != null){        
	        try{
	        	new DownloadImageTask(this).execute(url);
       	
	        }catch (Exception e){Log.e("PocketCurrency", "Application Exited Abnormally !");}
	    } 
	    else{
	    	Toast.makeText(this, "Connection Error. Please check your connections and try again.", Toast.LENGTH_SHORT).show();
	    	finish();
	    }
	    
	    
	    
	   // mGallery = (Gallery) findViewById(R.id.graph_gallery);
        
	    
   	   mTracker.dispatch();
     
    }
    /*
    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;


        public ImageAdapter(Context c) {
            mContext = c;
            TypedArray a = obtainStyledAttributes(R.styleable.Theme);
            mGalleryItemBackground = a.getResourceId(
                    R.styleable.Theme_android_galleryItemBackground, 0);
            a.recycle();
        }

        public int getCount() {
            return mBitmapList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);
            
            Display display = getWindowManager().getDefaultDisplay(); 
            int width = display.getWidth();
            int imageWidth = (width * 2) / 3;
            int imageHeight = Math.round(new Float(imageWidth * .8));


            //i.setImageResource(mImageIds[position]);
            i.setImageBitmap(mBitmapList.get(position));
            i.setLayoutParams(new Gallery.LayoutParams(imageWidth, imageHeight));
            i.setScaleType(ImageView.ScaleType.FIT_XY);
            i.setBackgroundResource(mGalleryItemBackground);

            return i;
        }
    }
    
    */
    
    
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
    	switch (requestCode) {
    	
	    	case GraphDuration.CHOOSE_TIMEFRAME:
	    		
	    		
	    		if(resultCode == GraphDuration.CHOOSE_TIMEFRAME_SUCCESS){

		    		mTimeframe = data.getExtras().getString("TIMEFRAME");
		    		
		    		if(mTickerLeft.compareTo(mTickerRight) == 0)
						mUrl = consts.get(mTimeframe).replace("TICKER", mTickerLeft);
					
					else
						mUrl = consts.get(mTimeframe).replace("TICKER", mTickerLeft) + "&c=" + mTickerRight;
		    		
	            
		            ConnectivityManager serviceConn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		    	    NetworkInfo networkInfo = serviceConn.getActiveNetworkInfo();
		    	    
		    	    Log.d(getLocalClassName(),"mUrl = " + mUrl);
		    	    
		    	    
		    	    TextView textView = (TextView)findViewById(R.id.graph_timeframe_1d);      
		    	      if(mTimeframe.compareTo("1 day") == 0)  	  
		    	    	  textView.setTextColor(Color.YELLOW);   	  
		    	      else 
		    	    	  textView.setTextColor(Color.WHITE); 
		    	      
		    	      
		    	      textView = (TextView)findViewById(R.id.graph_timeframe_5d);
		    	      if(mTimeframe.compareTo("5 day") == 0)
		    	    	  textView.setTextColor(Color.YELLOW);   	  
		    	      else 
		    	    	  textView.setTextColor(Color.WHITE); 
		    	      
		    	      textView = (TextView)findViewById(R.id.graph_timeframe_3m);
		    	      if(mTimeframe.compareTo("3 month") == 0)
		    	    	  textView.setTextColor(Color.YELLOW);   	 
		    	      else 
		    	    	  textView.setTextColor(Color.WHITE); 
		    	      
		    	      textView = (TextView)findViewById(R.id.graph_timeframe_1y);
		    	      if(mTimeframe.compareTo("1 year") == 0)
		    	    	  textView.setTextColor(Color.YELLOW);   	  
		    	      else 
		    	    	  textView.setTextColor(Color.WHITE); 
		    	      
		    	      textView = (TextView)findViewById(R.id.graph_timeframe_2y);
		    	      if(mTimeframe.compareTo("2 year") == 0)
		    	    	  textView.setTextColor(Color.YELLOW);   	  
		    	      else 
		    	    	  textView.setTextColor(Color.WHITE); 
		    	      
		    	      
		    	      textView = (TextView)findViewById(R.id.graph_timeframe_5y);
		    	      if(mTimeframe.compareTo("5 year") == 0)
		    	    	  textView.setTextColor(Color.YELLOW);   	  
		    	      else 
		    	    	  textView.setTextColor(Color.WHITE); 
		    	    	  
		    	    	 
		    	    
		    	    if(networkInfo != null){        
		    	        try{
		    	        	new DownloadImageTask(this).execute(mUrl);
		           	
		    	        }catch (Exception e){Log.e("PocketCurrency", "Application Exited Abnormally !");}
		    	    } 
		    	    else{
		    	    	Toast.makeText(this, "Connection Error. Please check your connections and try again.", Toast.LENGTH_SHORT).show();
		    	    	finish();
		    	    }
	            
	    		}
		    	
		    	
                
              default:
            	  break;
        }
    }
    
    private class DownloadImageTask extends AsyncTask {
    	
    	ProgressDialog mProgressDialog;
    	Context mContext;
    	
    	public DownloadImageTask(Context context){
    		
    		mContext = context;
    		
    		mProgressDialog = new ProgressDialog(context);
    		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       	 	mProgressDialog.setMessage("Generating Chart ... ");
       	 	mProgressDialog.show();
    		
    	}
		
	     public Object doInBackground(Object... urls) {
	        // return loadImageFromNetwork(urls[0]);
	    	 return URLFetcher.getBitmap((String)urls[0]);
	    	 
	     }

	     public void onPostExecute(Object result) {	    	 

	    	 
	    	 	if(result != null){
	    		 
	    		 Bitmap mBitmap = (Bitmap)result;
	    				int picw  = mBitmap.getWidth();
	    				int pich = mBitmap.getHeight();
	    				
	    				int[] pix = new int[picw * pich];
	    				
	    				mBitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);
	    				
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
	    					      
	    					      else if((g > 220 && g < 250) && (b > 230 && b < 260)){

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
	    		mImageButton.setImageBitmap(bm);
			    
			   // for(int i=0;i<6;i++)
			   // 	mBitmapList.add(bm);
			    
			    
			   // mGallery.setAdapter(new ImageAdapter(this.mContext));
			    
	    		 mProgressDialog.dismiss();
	    	 }  
	    	 
	    	 
	     }     
	}
    
    public final static HashMap<String, String> consts = new HashMap<String, String>();
    	static
    	{
    		consts.put("1 day","http://ichart.finance.yahoo.com/z?s=TICKER&t=1d&q=l&l=off&z=m&a=v&p=s");
        	consts.put("5 day","http://ichart.finance.yahoo.com/z?s=TICKER&t=5d&q=l&l=off&z=m&a=v&p=s");
        	consts.put("3 month","http://ichart.finance.yahoo.com/z?s=TICKER&t=3m&q=l&l=off&z=m&a=v&p=s");
        	consts.put("1 year","http://ichart.finance.yahoo.com/z?s=TICKER&t=1y&q=l&l=off&z=m&a=v&p=s");
        	consts.put("2 year","http://ichart.finance.yahoo.com/z?s=TICKER&t=2y&q=l&l=off&z=m&a=v&p=s");
        	consts.put("5 year","http://ichart.finance.yahoo.com/z?s=TICKER&t=5y&q=l&l=off&z=m&a=v&p=s");
    	}
	
	public final static String[] timeframeList = {"1d","5d","3m","1y","2y","5y"};
    
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
	
        super.onStart();  
}
   	
 
}