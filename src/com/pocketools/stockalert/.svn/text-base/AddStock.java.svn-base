package com.pocketools.stockalert;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

public class AddStock extends ListActivity {
	
	private View mAddStockView = null;
	private EditText mCompanyName;
	private EditText mAddSymbolDirectly;
	//private TableRow mCompanyResultRow;
	private MyAddCompanyAdapter adapter;
	private JSONObject[] mJSONRows = null;
	private ListView mListView;
	private TextView mEmptyResultView;
    	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        if(mAddStockView == null){
        	mAddStockView = LayoutInflater.from(this).inflate(R.layout.add_stock, null);
        }
        
        setContentView(mAddStockView);
        
        mCompanyName = (EditText)findViewById(R.id.add_stock_search);
   
        ImageButton search_button = (ImageButton)findViewById(R.id.add_stock_search_image);
        search_button.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {   
        		String companyName = mCompanyName.getText().toString();
        		
            	//Let's hide the keyboard if it's still showing
            	InputMethodManager imm = (InputMethodManager)getSystemService(v.getContext().INPUT_METHOD_SERVICE);
            	imm.hideSoftInputFromWindow(mCompanyName.getWindowToken(), 0);
            	
        		
        		if(companyName != null){
        			
        			
	                String escapedCompanyName = companyName.replace(" ", "%20");
	                String secondEscapedCompanyName = escapedCompanyName.replace("\n", "");
	                
	                String url = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=" + secondEscapedCompanyName + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";           
	        		new SearchTickerTask(v.getContext()).execute(url);   
        		}
       		}            	
		});
        
        
        mAddSymbolDirectly = (EditText)findViewById(R.id.add_stock_symbol_directly);
        
        Button addDirectly = (Button)findViewById(R.id.add_stock_directly_button);
        addDirectly.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {   
        		String symbolDirectly = mAddSymbolDirectly.getText().toString();
        		
        		if(symbolDirectly != null && symbolDirectly.length() > 0){
        			
        			String symbolUpperCase = symbolDirectly.toUpperCase();
        			       
        			DBAdapter db = new DBAdapter(getApplicationContext());
                    db.open();
                                       
                    db.addStock(symbolUpperCase, "");	                    
                    db.close();
                    
                    Toast.makeText(v.getContext(), symbolUpperCase + " Added", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(v.getContext(),StockAlert.class);
                    intent.putExtra("FORCE_REFRESH", true);
                    startActivity(intent);
                    
                    //Let's finish the current activity
                    finish();
        			
        		}
       		}            	
		});
        
        
        /*
        mCompanyResultRow = (TableRow)findViewById(R.id.add_stock_company_row);
        
        //Add a row to avoid an exception when "removing all views" later on
        TextView view = new TextView(this);
        mCompanyResultRow.addView(view);
        */
        
        if(mJSONRows == null)
        	mJSONRows = new JSONObject[0];
  
        
        ListAdapter listAdapter = new MyAddCompanyAdapter(this,R.layout.company_list,mJSONRows);
        mListView = getListView();        
        mListView.setAdapter(listAdapter);
        
        mEmptyResultView = (TextView)findViewById(R.id.add_stock_empty);
 
    }
    
    
    
    private class SearchTickerTask extends AsyncTask {
    	
    	ProgressDialog mProgressDialog;
    	Context mContext;
    	
    	public SearchTickerTask(Context context){
    		
    		mContext = context;
    		
    		mProgressDialog = new ProgressDialog(context);
    		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       	 	mProgressDialog.setMessage("Searching for Company ... ");
       	 	mProgressDialog.show();
    		
    	}
		
	     public Object doInBackground(Object... urls) {
	    	 return URLFetcher.getString((String)urls[0]);
	    	 
	     }

	     public void onPostExecute(Object result) {	  
	    		    	 

	    	 
	    	 if(result != null){
	    		     		 
	    		 
	    		 try{
	    			 
	    			 String stringResult = (String)result;
	    			 String jsonResult = stringResult.substring(stringResult.indexOf("["),stringResult.indexOf("}})"));

		    		 JSONArray array = new JSONArray(jsonResult);
		    		 
		    		 JSONObject[] jsonList = new JSONObject[array.length()];
		    		 
		    		 for(int i=0;i< array.length();i++){
		    			 jsonList[i] = array.getJSONObject(i); 
		    		 }
		    		 
		    		 mJSONRows = jsonList;	    		 		 
	    		 
	    		 }catch(JSONException e){
	    			 Log.d(getLocalClassName(),"Exception thrown : " + e.getMessage());
	    			 Toast.makeText(mContext, "Error finding company name", Toast.LENGTH_LONG).toString();
	    		 }
		    	 catch(Exception e){
	    			 Log.d(getLocalClassName(),"Exception thrown : ");
	    			 Toast.makeText(mContext, "Error finding company name", Toast.LENGTH_LONG).toString();
	    		 }
	    		    		 
	    		 
	    		 if(mJSONRows.length < 1){
	    			 mEmptyResultView.setText("Cannot find Company Name, Symbol or Index =( ");
	    		 }
	    		 else
	    			 mEmptyResultView.setText("");

	    		 setContentView(mAddStockView);
	    		 
	    		 ListAdapter listAdapter = new MyAddCompanyAdapter(mContext,R.layout.company_list,mJSONRows);
	    		 mListView.setAdapter(listAdapter);
	    		 
	    		 
    		 
	    	 }  
	    	 
	    	 
    		 mProgressDialog.dismiss();
    				    	 
	     }     
	     
	}
    
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
    	  if (keyCode == KeyEvent.KEYCODE_BACK) { 
    		  
    		  Intent intent = new Intent(this,StockAlert.class);
              startActivity(intent);            
              //Let's finish the current activity
              finish();            
              
    	  }  
    	  return true;
    	} 
    
	 
	 
	 
    
    
    private class MyAddCompanyAdapter extends ArrayAdapter<JSONObject>{
    	
    	private JSONObject[] mJSONRows;
    	
 
    	public MyAddCompanyAdapter(Context context, int resourceId, JSONObject[] jsonRows) {
            super(context,resourceId,jsonRows);
            this.mJSONRows = jsonRows;
    	}
    	

    	 @Override
         public View getView(int position, View convertView, ViewGroup parent) {
                 View v = convertView;
                 if (v == null) {
                         LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                 v = vi.inflate(R.layout.company_list, null);
                 }
                              
                 String resultSymbol = "";
                 String resultCompanyName = "";
                 String exchange = "";
                 

                 JSONObject json = mJSONRows[position];
                 if (json != null) {
                	 
             	 	try{
             	 		resultSymbol = json.getString("symbol");
             	 		resultCompanyName = json.getString("name");
             	 		exchange = json.getString("exchDisp");
                         
            	 	}catch(JSONException e){}
            	 	
                     TextView view = (TextView) v.findViewById(R.id.add_symbol);
                     view.setText(resultSymbol + " (" + exchange + ")");
                     
                     view = (TextView)v.findViewById((R.id.add_company_name));
                     view.setText(resultCompanyName);
                     
                     StockUtil util = new StockUtil(resultSymbol,resultCompanyName);
                     v.setTag(util);
                 }
                 
                 v.setOnClickListener(new TextView.OnClickListener() {
	            	public void onClick(View v) {	        
	            		
	            		v.setBackgroundColor(Color.parseColor("#FF9900"));
	                    DBAdapter db = new DBAdapter(getApplicationContext());
	                    db.open();
	                    
	                    StockUtil util = (StockUtil)v.getTag();
	                    db.addStock(util.getSymbol(), util.getCompanyName());	                    
	                    db.close();
	                    
	                    Toast.makeText(v.getContext(), util.getCompanyName() + " Added", Toast.LENGTH_SHORT).show();
	                    
	                    Intent intent = new Intent(v.getContext(),StockAlert.class);
	                    intent.putExtra("FORCE_REFRESH", true);
	                    startActivity(intent);
	                    
	                    //Let's finish the current activity
	                    finish();
	            		
               		}            	
        		});
                 
                 return v;
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

        super.onPause();
    }
    
    @Override
    protected void onStop() {      
        Log.d(this.getLocalClassName(), "onStop()");
  
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
        Log.d(this.getLocalClassName(), "onDestroy()");
        
        super.onDestroy();
        

    }
    
    @Override
    protected void onResume() {
    	Log.d(this.getLocalClassName(), "onResume()");

    	
    	super.onResume();
    }
    
    @Override
    protected void onRestart() {        
    	Log.d(this.getLocalClassName(), "onRestart()");

      
        super.onRestart();
    }
    
    
    protected void onStart() {
    	Log.d(this.getLocalClassName(), "onStart()");
	
        super.onStart();  
}

}
