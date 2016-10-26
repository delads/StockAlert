package com.pocketools.stockalert;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.AdapterView.OnItemSelectedListener;




public class ChooseIndex extends Activity {
    /** Called when the activity is first created. */
	/*
	boolean mUSIndicesIsExpanded = false;
	boolean mAmericasIndicesIsExpanded = false;
	boolean mAsiaIndicesIsExpanded = false;
	boolean mEuropeIndicesIsExpanded = false;
	boolean mAfricaIndicesIsExpanded = false;
	TableRow mUSRow;
	ImageView mUSArrow;
	TableRow mAmericasRow;
	ImageView mAmericasArrow;
	TableRow mAsiaRow;
	ImageView mAsiaArrow;
	TableRow mEuropeRow;
	ImageView mEuropeArrow;
	TableRow mAfricaRow;
	ImageView mAfricaArrow;
	*/
	
	Spinner mUSSpinner;
	ArrayAdapter mUSAdapter;
	Spinner mAmericasSpinner;
	ArrayAdapter mAmericasAdapter;
	Spinner mAsiaSpinner;
	ArrayAdapter mAsiaAdapter;
	Spinner mEuropeSpinner;
	ArrayAdapter mEuropeAdapter;
	Spinner mAfricaSpinner;
	ArrayAdapter mAfricaAdapter;
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);       
        setContentView(R.layout.change_index);
        
        
        mUSSpinner = (Spinner) findViewById(R.id.us_index_spinner);
        mUSAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, usArray);
        mUSAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUSSpinner.setAdapter(mUSAdapter);
        mUSSpinner.setSelection(0, false);
        //mUSSpinner.setSelection(0);
        
        mUSSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v, int position,
                long id) {
          	  	String longCurrency = (String)mUSAdapter.getItem(position);
          	  	String symbol = longCurrency.substring(0,longCurrency.indexOf(" "));
          	  	String companyName = longCurrency.substring(longCurrency.indexOf(" "),longCurrency.length());

        	  	Intent intent = new Intent(v.getContext(),Markets.class);
        	  	intent.putExtra("SYMBOL", symbol);
        	  	intent.putExtra("COMPANY_NAME", companyName);
          	  	startActivity(intent);
          	  	finish();
          	  
            }
  	        public void onNothingSelected(AdapterView arg0) {
  	          // NOP
  	        }
  	      });
        
        
        mAmericasSpinner = (Spinner) findViewById(R.id.americas_index_spinner);
        mAmericasAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, americasArray);
        mAmericasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAmericasSpinner.setAdapter(mAmericasAdapter);
        mAmericasSpinner.setSelection(0, false);
        //mUSSpinner.setSelection(0);
        
        mAmericasSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v, int position,
                long id) {
          	  	String longCurrency = (String)mAmericasAdapter.getItem(position);
          	  	String symbol = longCurrency.substring(0,longCurrency.indexOf(" "));
          	  	String companyName = longCurrency.substring(longCurrency.indexOf(" "),longCurrency.length());

        	  	Intent intent = new Intent(v.getContext(),Markets.class);
        	  	intent.putExtra("SYMBOL", symbol);
        	  	intent.putExtra("COMPANY_NAME", companyName);
          	  	startActivity(intent);
          	  	finish();
          	  
            }
  	        public void onNothingSelected(AdapterView arg0) {
  	          // NOP
  	        }
  	      });
        
        mAsiaSpinner = (Spinner) findViewById(R.id.asia_index_spinner);
        mAsiaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, asiaArray);
        mAsiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAsiaSpinner.setAdapter(mAsiaAdapter);
        mAsiaSpinner.setSelection(0, false);
        //mUSSpinner.setSelection(0);
        
        mAsiaSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v, int position,
                long id) {
          	  	String longCurrency = (String)mAsiaAdapter.getItem(position);
          	  	String symbol = longCurrency.substring(0,longCurrency.indexOf(" "));
          	    String companyName = longCurrency.substring(longCurrency.indexOf(" "),longCurrency.length());

        	  	Intent intent = new Intent(v.getContext(),Markets.class);
        	  	intent.putExtra("SYMBOL", symbol);
        	  	intent.putExtra("COMPANY_NAME", companyName);
          	  	startActivity(intent);
          	  	finish();
          	  
            }
  	        public void onNothingSelected(AdapterView arg0) {
  	          // NOP
  	        }
  	      });
        
        mEuropeSpinner = (Spinner) findViewById(R.id.europe_index_spinner);
        mEuropeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, europeArray);
        mEuropeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEuropeSpinner.setAdapter(mEuropeAdapter);
        mEuropeSpinner.setSelection(0, false);
        //mUSSpinner.setSelection(0);
        
        mEuropeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v, int position,
                long id) {
          	  	String longCurrency = (String)mEuropeAdapter.getItem(position);
          	  	String symbol = longCurrency.substring(0,longCurrency.indexOf(" "));
          	  	String companyName = longCurrency.substring(longCurrency.indexOf(" "),longCurrency.length());

          	  	Intent intent = new Intent(v.getContext(),Markets.class);
          	  	intent.putExtra("SYMBOL", symbol);
          	  	intent.putExtra("COMPANY_NAME", companyName);
          	  	startActivity(intent);
          	  	finish();
          	  
            }
  	        public void onNothingSelected(AdapterView arg0) {
  	          // NOP
  	        }
  	      });
        
        mAfricaSpinner = (Spinner) findViewById(R.id.africa_index_spinner);
        mAfricaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, africaArray);
        mAfricaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAfricaSpinner.setAdapter(mAfricaAdapter);
        mAfricaSpinner.setSelection(0, false);
        //mUSSpinner.setSelection(0);
        
        mAfricaSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v, int position,
                long id) {
          	  	String longCurrency = (String)mAfricaAdapter.getItem(position);
          	  	String symbol = longCurrency.substring(0,longCurrency.indexOf(" "));
          	  	String companyName = longCurrency.substring(longCurrency.indexOf(" "),longCurrency.length());

        	  	Intent intent = new Intent(v.getContext(),Markets.class);
        	  	intent.putExtra("SYMBOL", symbol);
        	  	intent.putExtra("COMPANY_NAME", companyName);
          	  	startActivity(intent);
          	  	finish();
          	  
            }
  	        public void onNothingSelected(AdapterView arg0) {
  	          // NOP
  	        }
  	      });
        
        
        /*
        
        mUSRow = (TableRow)findViewById(R.id.expandable_us_indices);
        mUSRow.setVisibility(View.GONE);
               
        mUSArrow = (ImageView)findViewById(R.id.arrow_us_indices);
        mUSArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        
        mAmericasRow = (TableRow)findViewById(R.id.expandable_americas_indices);
        mAmericasRow.setVisibility(View.GONE);
               
        mAmericasArrow = (ImageView)findViewById(R.id.arrow_americas_indices);
        mAmericasArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        
        mAsiaRow = (TableRow)findViewById(R.id.expandable_asia_indices);
        mAsiaRow.setVisibility(View.GONE);
               
        mAsiaArrow = (ImageView)findViewById(R.id.arrow_asia_indices);
        mAsiaArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        
        mEuropeRow = (TableRow)findViewById(R.id.expandable_europe_indices);
        mEuropeRow.setVisibility(View.GONE);
               
        mEuropeArrow = (ImageView)findViewById(R.id.arrow_europe_indices);
        mEuropeArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        
        mAfricaRow = (TableRow)findViewById(R.id.expandable_africa_indices);
        mAfricaRow.setVisibility(View.GONE);
               
        mAfricaArrow = (ImageView)findViewById(R.id.arrow_africa_indices);
        mAfricaArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        
        
        
        TableRow usIndexHeader = (TableRow)findViewById(R.id.click_us_indices);
        usIndexHeader.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		
        		if(mUSIndicesIsExpanded == true){
        			mUSRow.setVisibility(View.GONE);
        			mUSIndicesIsExpanded = false;
        			mUSArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        		}
        		else{
        			mUSRow.setVisibility(View.VISIBLE);
        			mUSIndicesIsExpanded = true;
        			mUSArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        		}
       		}      
        	
        });
        
        
        TableRow americasIndexHeader = (TableRow)findViewById(R.id.click_americas_indices);
        americasIndexHeader.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		
        		if(mAmericasIndicesIsExpanded == true){
        			mAmericasRow.setVisibility(View.GONE);
        			mAmericasIndicesIsExpanded = false;
        			mAmericasArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        		}
        		else{
        			mAmericasRow.setVisibility(View.VISIBLE);
        			mAmericasIndicesIsExpanded = true;
        			mAmericasArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        		}
       		}      
        	
        });
        
        
        TableRow asiaIndexHeader = (TableRow)findViewById(R.id.click_asia_indices);
        asiaIndexHeader.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		
        		if(mAsiaIndicesIsExpanded == true){
        			mAsiaRow.setVisibility(View.GONE);
        			mAsiaIndicesIsExpanded = false;
        			mAsiaArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        		}
        		else{
        			mAsiaRow.setVisibility(View.VISIBLE);
        			mAsiaIndicesIsExpanded = true;
        			mAsiaArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        		}
       		}      
        	
        });
        
        
        TableRow europeIndexHeader = (TableRow)findViewById(R.id.click_europe_indices);
        europeIndexHeader.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		
        		if(mEuropeIndicesIsExpanded == true){
        			mEuropeRow.setVisibility(View.GONE);
        			mEuropeIndicesIsExpanded = false;
        			mEuropeArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        		}
        		else{
        			mEuropeRow.setVisibility(View.VISIBLE);
        			mEuropeIndicesIsExpanded = true;
        			mEuropeArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        		}
       		}      
        	
        });
        
        
        TableRow africaIndexHeader = (TableRow)findViewById(R.id.click_africa_indices);
        africaIndexHeader.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		
        		if(mAfricaIndicesIsExpanded == true){
        			mAfricaRow.setVisibility(View.GONE);
        			mAfricaIndicesIsExpanded = false;
        			mAfricaArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
        		}
        		else{
        			mAfricaRow.setVisibility(View.VISIBLE);
        			mAfricaIndicesIsExpanded = true;
        			mAfricaArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        		}
       		}      
        	
        });
        
        
        */
    }
    
    private String[] usArray = {  "US Indices",
	        "^DJA   Dow Jones Composite Average",
			"^DJI   Dow Jones Industrial Average",
			"^DJT   Dow Jones Transportation Averag",
			"^DJU   Dow Jones Utility Average",   
    		"^NYA   NYSE COMPOSITE INDEX (NEW METHO", 
    		"^NIN   NYSE International 100", 
    		"^NTM   NYSE TMT", 
    		"^NUS   NYSE US 100", 
    		"^NWL   NYSE World Leaders", 
    		"^TV.N  Volume in 000's", 	
    		"^IXBK  NASDAQ Bank", 
    		"^NBI   NASDAQ Biotechnology", 
    		"^IXIC  NASDAQ Composite", 
    		"^IXK   NASDAQ Computer", 
    		"^IXF   NASDAQ Financial 100", 
    		"^IXID  NASDAQ Industrial", 
    		"^IXIS  NASDAQ Insurance", 
    		"^IXFN  NASDAQ Other Finance", 
    		"^IXUT  NASDAQ Telecommunications", 
    		"^IXTR  NASDAQ Transportation", 
    		"^NDX   NASDAQ-100", 
    		"^TV.O  Volume in 000's", 
    		"^OEX   S&P 100 INDEX", 
    		"^MID   S&P 400 MIDCAP INDEX", 
    		"^GSPC  S&P 500 INDEX,RTH", 
    		"^SPSUPX   S&P COMPOSITE 1500 INDEX", 
    		"^SML   S&P SMALLCAP 600 INDEX", 
    		"^XAX   AMEX COMPOSITE INDEX", 
    		"^IIX   AMEX INTERACTIVE WEEK INTERNET", 
    		"^NWX   AMEX NETWORKING INDEX", 
    		"^DWC   DJ WILSHIRE 5000 TOT", 
    		"^XMI   MAJOR MARKET INDEX", 
    		"^PSE   NYSE Arca Tech 100 Index", 
    		"^SOX   PHLX Semiconductor", 
    		"^RUI   RUSSELL 1000 INDEX", 
    		"^RUT   RUSSELL 2000 INDEX", 
    		"^RUA   RUSSELL 3000 INDEX", 
    		"^IRX   13-WEEK TREASURY BILL", 
    		"^TNX   CBOE Interest Rate 10-Year T-No", 
    		"^TYX   Treasury Yield 30 Years", 
    		"^FVX   Treasury Yield 5 Years", 
    		"GIM10.CME   Goldman Sachs Commodity Jun 10", 
    		"^XAU   PHLX Gold/Silver Sector", 
    		"RCIV10.CME  Rogers Int'l Commodity Oct 10"};
    		
    
    private String[] americasArray = {  "Americas",
	        "^MERV  MerVal",
			"^BVSP  Bovespa",
			"^GSPTSE   S&P TSX Composite",
			"^MXX   IPC",
			"^GSPC  500 Index"};
    
    private String[] asiaArray = {  "Asia / Pacific",
    		"^AORD  All Ordinaries",
    		"^SSEC  Shanghai Composite",
    		"^HSI   Hang Seng",
    		"^BSESN BSE 30",
    		"^JKSE  Jakarta Composite",
    		"^KLSE  KLSE Composite",
    		"^N225  Nikkei 225",
    		"^NZ50  NZSE 50",
    		"^STI   Straits Times",	
    		"^KS11  Seoul Composite",
    		"^TWII  Taiwan Weighted"};
    
    private String[] europeArray = {  "Europe",
    		"^ISEQ  ISEQ Index",
    		"^FTSE  FTSE 100",
    		"^ATX   ATX",
    		"^BFX   BEL-20",
    		"^FCHI  CAC 40",
    		"^GDAXI DAX",
    		"^AEX   AEX General",
    		"^OSEAX OSE All Share",
    		"^SMSI  Madrid General",
    		"^OMXSPI  Stockholm General",
    		"^SSMI  Swiss Market"};
    		
    
    private String[] africaArray = {  "Africa / Middle East",
	        "CMA.CA ",
			"^TA100 TA-100"};
 
}