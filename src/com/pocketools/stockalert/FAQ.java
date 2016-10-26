package com.pocketools.stockalert;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableRow;




public class FAQ extends Activity {
    /** Called when the activity is first created. */
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //requestWindowFeature(Window.FEATURE_NO_TITLE); 
       // setTheme(android.R.style.Theme_Panel);
        setContentView(R.layout.faq);
        
              
        Button buttonContinue = (Button)findViewById(R.id.faq_button_continue);
        buttonContinue.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		
        		finish();
       		}            	
		});
        
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